import os
import shutil
import numpy as np
from PIL import Image

BASE_DIR = r"c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable")
WORK_DIR = os.path.join(BASE_DIR, "work_images")
AUDIT_OUT_DIR = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "13_Audit_Tombol_QRIS_Navigasi_Bawah_100Persen")
BEFORE_DIR = os.path.join(AUDIT_OUT_DIR, "before_assets")

os.makedirs(AUDIT_OUT_DIR, exist_ok=True)
os.makedirs(BEFORE_DIR, exist_ok=True)

def backup_original_drawables():
    print("Checking backup of original drawables...")
    drawables = [
        "bca_nav_bar_home.png",
        "bca_nav_bar_transaksi.png",
        "bca_nav_bar_notifikasi.png",
        "bca_nav_bar_akun_saya.png",
        "bca_nav_bar_all_grey.png"
    ]
    for d in drawables:
        src = os.path.join(RES_DIR, d)
        dst = os.path.join(BEFORE_DIR, d)
        if os.path.exists(src) and not os.path.exists(dst):
            shutil.copyfile(src, dst)
            print(f"  Backed up {d} to {BEFORE_DIR}")

def build_perfect_circle_qris_navbar():
    """
    Synthesizes a 100% mathematically authentic PERFECT CIRCLE QRIS dome.
    BCA Mobile authentic button geometry:
    - Pure Circle Center: (Xc, Yc) = (359.1, 74.6)
    - Outer White Rim Radius: Rout = 46.2 px (Diameter = 92.4 px)
    - Apex Y: 74.6 - 46.2 = 28.4 px (Elevation above flat Y=64: 35.6 px)
    - Horizontal Dome Span at Y=64: X in [314.1 .. 404.1] (Width = 90.0 px)
    - Inner Blue Button Radius: Rin = 40.0 px (Diameter = 80.0 px)
    - Pure Clean White Rim: [242, 252, 255]
    - 16x16 Subpixel Analytical Supersampling (256 samples/px)
    - Zero dark navy [40, 62, 85] smudges and zero lateral white bloating.
    """
    print("Synthesizing mathematically authentic PERFECT CIRCLE QRIS dome...")
    im_exact = Image.open(os.path.join(WORK_DIR, "references", "bca_nav_exact.png"))
    arr_exact = np.array(im_exact).astype(float)
    
    H, W = 200, 720
    alpha = np.zeros((H, W), dtype=np.float32)
    fg_rgb = np.copy(arr_exact)
    
    # Exact Circle Model
    xc, yc = 359.1, 74.6
    r_out = 46.2
    r_in = 40.0
    
    # 16x16 subpixel grid
    sub = np.linspace(0.5/16, 1.0 - 0.5/16, 16)
    sub_x, sub_y = np.meshgrid(sub, sub)
    sub_x = sub_x.flatten()
    sub_y = sub_y.flatten()
    
    # Flat navbar region y >= 64
    alpha[64:, :] = 1.0
    
    # Subpixel anti-aliasing for the flat line at y=63
    for x in range(W):
        if x < 312 or x > 406:
            alpha[63, x] = 0.15
            
    pure_white_rim = np.array([242.0, 252.0, 255.0])
    
    for y in range(20, 64):
        for x in range(305, 415):
            xs = x + sub_x
            ys = y + sub_y
            dist = np.sqrt((xs - xc)**2 + (ys - yc)**2)
            inside = (dist <= r_out) | (ys >= 64.0)
            cov = np.sum(inside) / 256.0
            alpha[y, x] = cov
            
            # Center of the current pixel
            cdist = np.sqrt((x + 0.5 - xc)**2 + (y + 0.5 - yc)**2)
            
            if cdist <= r_in:
                # Deep interior: authentic blue button graphic, QRIS logo and text
                fg_rgb[y, x] = arr_exact[y, x]
            elif cdist <= r_out + 0.5 and cov > 0.0:
                # White rim zone: replace any dark navy background contamination
                if cdist <= 42.5:
                    t = (cdist - r_in) / (42.5 - r_in)
                    fg_rgb[y, x] = (1.0 - t) * arr_exact[y, x] + t * pure_white_rim
                else:
                    fg_rgb[y, x] = pure_white_rim
            else:
                if cov == 0.0:
                    fg_rgb[y, x] = 0.0
                    
    master_rgba = np.zeros((H, W, 4), dtype=np.uint8)
    master_rgba[:, :, :3] = np.clip(fg_rgb, 0, 255).astype(np.uint8)
    master_rgba[:, :, 3] = np.clip(alpha * 255.0, 0, 255).astype(np.uint8)
    
    return master_rgba, alpha

def update_all_nav_drawables(master_rgba, alpha):
    print("Updating all 5 nav drawables with authentic PERFECT CIRCLE QRIS dome...")
    
    # 1. Home Active: master_rgba
    im_home = Image.fromarray(master_rgba, mode='RGBA')
    im_home.save(os.path.join(RES_DIR, "bca_nav_bar_home.png"))
    print("  Saved bca_nav_bar_home.png")
    
    other_drawables = [
        "bca_nav_bar_transaksi.png",
        "bca_nav_bar_notifikasi.png",
        "bca_nav_bar_akun_saya.png",
        "bca_nav_bar_all_grey.png"
    ]
    
    for d in other_drawables:
        before_path = os.path.join(BEFORE_DIR, d)
        if not os.path.exists(before_path):
            before_path = os.path.join(RES_DIR, d)
            
        orig_arr = np.array(Image.open(before_path)).copy()
        
        # Replace the QRIS region (columns 305..414, all rows) with master_rgba
        orig_arr[:, 305:415, :] = master_rgba[:, 305:415, :]
        
        # Apply the master alpha and top boundary for y < 64 across all columns
        orig_arr[:64, :, :] = master_rgba[:64, :, :]
        
        # Ensure alpha for y >= 64 is solid 255
        orig_arr[64:, :, 3] = 255
        
        im_updated = Image.fromarray(orig_arr, mode='RGBA')
        im_updated.save(os.path.join(RES_DIR, d))
        print(f"  Saved {d}")

if __name__ == "__main__":
    backup_original_drawables()
    master_rgba, alpha = build_perfect_circle_qris_navbar()
    update_all_nav_drawables(master_rgba, alpha)
    print("Nav drawables successfully updated with PERFECT CIRCLE QRIS dome.")
