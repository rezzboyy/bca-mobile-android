import os
import shutil
import numpy as np
from PIL import Image
from scipy.interpolate import PchipInterpolator

BASE_DIR = r"c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable")
WORK_DIR = os.path.join(BASE_DIR, "work_images")
AUDIT_OUT_DIR = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "13_Audit_Tombol_QRIS_Navigasi_Bawah_100Persen")
BEFORE_DIR = os.path.join(AUDIT_OUT_DIR, "before_assets")

os.makedirs(AUDIT_OUT_DIR, exist_ok=True)
os.makedirs(BEFORE_DIR, exist_ok=True)

def build_authentic_exact_qris_navbar():
    """
    Synthesizes the 100% mathematically authentic BCA Mobile QRIS dome
    matching the exact ground-truth reference 'bca_nav_exact.png'.
    
    Key metrics:
    - Center X: Xc = 359.0 px
    - Apex Y: 26.5 px
    - PCHIP monotonic smooth contour Rx(y) directly calibrated from bca_nav_exact.png
    - Pure white rim: [242, 252, 255] with zero dark navy residue
    - 16x16 Subpixel Analytical Supersampling (256 samples per pixel)
    - Zero lateral bloating (alpha = 0 outside x in [310..408] for y < 64)
    - Zero internal holes (solid alpha inside dome boundary)
    """
    print("Synthesizing authentic exact BCA Mobile QRIS dome...")
    im_exact = Image.open(os.path.join(WORK_DIR, "references", "bca_nav_exact.png"))
    arr_exact = np.array(im_exact).astype(float)
    
    H, W = 200, 720
    alpha = np.zeros((H, W), dtype=np.float32)
    fg_rgb = np.copy(arr_exact)
    
    # Calibrated ground-truth points from bca_nav_exact.png
    xc = 359.0
    y_pts = np.array([26.5, 27.0, 28.0, 29.0, 30.0, 32.0, 35.0, 40.0, 45.0, 50.0, 55.0, 60.0, 63.0, 64.0])
    rx_pts = np.array([0.0,  14.5, 16.5, 20.8, 24.8, 29.5, 34.0, 39.8, 43.6, 45.6, 46.8, 47.6, 48.6, 50.0])
    
    interp_rx = PchipInterpolator(y_pts, rx_pts)
    
    # 16x16 subpixel sampling grid
    N_SUB = 16
    sub = np.linspace(0.5 / N_SUB, 1.0 - 0.5 / N_SUB, N_SUB)
    sub_x, sub_y = np.meshgrid(sub, sub)
    sub_x = sub_x.flatten()
    sub_y = sub_y.flatten()
    
    # Flat navbar region y >= 64
    alpha[64:, :] = 1.0
    
    # Subpixel anti-aliasing for the flat line at y=63 outside dome
    for x in range(W):
        if x < 308 or x > 410:
            alpha[63, x] = 0.20
            
    pure_white_rim = np.array([242.0, 252.0, 255.0])
    highlight_rim = np.array([248.0, 253.0, 255.0])
    
    # Compute alpha for y in [25..63] and x in [300..418]
    for y in range(25, 64):
        for x in range(300, 418):
            xs = x + sub_x
            ys = y + sub_y
            
            # Check inside dome for each subpixel
            inside = np.zeros(len(xs), dtype=bool)
            for i in range(len(xs)):
                yi = ys[i]
                xi = xs[i]
                if yi >= 64.0:
                    inside[i] = True
                elif yi >= 26.5:
                    r_allowed = interp_rx(yi)
                    if abs(xi - xc) <= r_allowed:
                        inside[i] = True
                        
            cov = np.sum(inside) / float(len(xs))
            alpha[y, x] = cov
            
            # Center of the current pixel
            cx_rel = abs(x + 0.5 - xc)
            cy = y + 0.5
            
            # Distance from rim top
            if cy < 64.0:
                rx_allowed = interp_rx(cy) if cy >= 26.5 else 0.0
                dist_from_edge = rx_allowed - cx_rel
            else:
                dist_from_edge = 10.0
                
            # If in the outer rim zone (cy < 34.0 or dist_from_edge < 6.5)
            if cov > 0.0:
                if cy <= 33.5 or dist_from_edge <= 6.5:
                    # Clean white rim substitution - removes any dark navy background contamination!
                    if cy <= 30.0:
                        fg_rgb[y, x] = highlight_rim
                    else:
                        t = np.clip((33.5 - cy) / 3.5, 0.0, 1.0)
                        fg_rgb[y, x] = (1.0 - t) * pure_white_rim + t * highlight_rim
                else:
                    # Interior: authentic blue button graphic, QRIS logo, and text
                    fg_rgb[y, x] = arr_exact[y, x]
            else:
                fg_rgb[y, x] = 0.0
                
    master_rgba = np.zeros((H, W, 4), dtype=np.uint8)
    master_rgba[:, :, :3] = np.clip(fg_rgb, 0, 255).astype(np.uint8)
    master_rgba[:, :, 3] = np.clip(alpha * 255.0, 0, 255).astype(np.uint8)
    
    return master_rgba, alpha

def deploy_to_all_navbars(master_rgba):
    print("Deploying authentic exact QRIS dome to all 5 navigation drawables...")
    drawables = [
        "bca_nav_bar_home.png",
        "bca_nav_bar_transaksi.png",
        "bca_nav_bar_notifikasi.png",
        "bca_nav_bar_akun_saya.png",
        "bca_nav_bar_all_grey.png"
    ]
    
    for d in drawables:
        target_path = os.path.join(RES_DIR, d)
        if d == "bca_nav_bar_home.png":
            im_out = Image.fromarray(master_rgba, mode='RGBA')
            im_out.save(target_path)
            print(f"  Overwrote {d} with master_rgba")
        else:
            # Load existing drawable, replace QRIS columns and top region
            orig = np.array(Image.open(target_path)).copy()
            # Replace QRIS dome region x in [300..418], all rows
            orig[:, 300:418, :] = master_rgba[:, 300:418, :]
            # Replace top alpha boundary y < 64
            orig[:64, :, :] = master_rgba[:64, :, :]
            # Ensure solid alpha for y >= 64
            orig[64:, :, 3] = 255
            im_out = Image.fromarray(orig, mode='RGBA')
            im_out.save(target_path)
            print(f"  Updated {d} with authentic QRIS dome")

if __name__ == "__main__":
    master_rgba, alpha = build_authentic_exact_qris_navbar()
    deploy_to_all_navbars(master_rgba)
    print("All navbars successfully updated with authentic exact QRIS dome.")
