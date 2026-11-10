import os
import shutil
import numpy as np
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = r"c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
RES_DIR = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable")
WORK_DIR = os.path.join(BASE_DIR, "work_images")
AUDIT_OUT_DIR = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "13_Audit_Tombol_QRIS_Navigasi_Bawah_100Persen")
BEFORE_DIR = os.path.join(AUDIT_OUT_DIR, "before_assets")

os.makedirs(AUDIT_OUT_DIR, exist_ok=True)
os.makedirs(BEFORE_DIR, exist_ok=True)

def backup_original_drawables():
    print("Backing up original drawables...")
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

def build_perfect_qris_navbar():
    """
    Synthesizes a 100% mathematically flawless QRIS dome and alpha mask.
    Apex curvature is continuous with authentic 16x16 subpixel analytical anti-aliasing.
    Zero navy/dark halo or smudge at apex and along the squircle boundary.
    Pure clean white rim [242, 252, 255] seamlessly blends into light and dark backgrounds.
    """
    print("Synthesizing mathematically perfect clean QRIS dome...")
    im_exact = Image.open(os.path.join(WORK_DIR, "references", "bca_nav_exact.png"))
    arr_exact = np.array(im_exact).astype(float)
    
    H, W = 200, 720
    alpha = np.zeros((H, W), dtype=np.float32)
    fg_rgb = np.copy(arr_exact)
    
    # Mathematical Squircle Model (Superellipse) derived from real BCA contour fitting
    # Apex = yc - b = 82.5 - 55.0 = 27.5 px (continuous subpixel apex)
    # Flat navbar = 64.0 px (elevation = 36.5 px)
    xc, yc = 359.0, 82.5
    a, b, n = 51.5, 55.0, 2.40
    
    # 16x16 subpixel grid for analytical coverage anti-aliasing
    sub = np.linspace(0.5/16, 1.0 - 0.5/16, 16)
    sub_x, sub_y = np.meshgrid(sub, sub)
    sub_x = sub_x.flatten()
    sub_y = sub_y.flatten()
    
    # Solid flat navbar region
    alpha[64:, :] = 1.0
    
    # Subpixel anti-aliasing for the flat top edge at y=63
    for x in range(W):
        if x < 307 or x > 411:
            alpha[63, x] = 0.15
            
    # Pure clean white rim color reference from authentic BCA
    pure_white_rim = np.array([242.0, 252.0, 255.0])
    
    # Process dome region (y < 64, x in [295..423])
    for y in range(20, 64):
        for x in range(295, 424):
            xs = x + sub_x
            ys = y + sub_y
            
            sq = (np.abs(xs - xc) / a)**n + (np.abs(ys - yc) / b)**n
            r_norm = sq**(1.0 / n)
            
            # Subpixel coverage inside the dome boundary (r_norm <= 1.0) or flat bar
            inside = (r_norm <= 1.0) | (ys >= 64.0)
            cov = np.sum(inside) / 256.0
            alpha[y, x] = cov
            
            # Center of the current pixel in squircle metric
            center_dx = abs(x + 0.5 - xc) / a
            center_dy = abs(y + 0.5 - yc) / b
            center_r = (center_dx**n + center_dy**n)**(1.0 / n)
            
            if center_r <= 0.94:
                # Deep interior of button: retain 100% authentic blue button graphic,
                # QRIS logo and QRIS text from bca_nav_exact
                fg_rgb[y, x] = arr_exact[y, x]
            elif center_r <= 1.05 and cov > 0.0:
                # Outer rim zone: replace any navy background contamination
                # with pure clean white rim color!
                if center_r <= 0.965:
                    # Smooth blend between inner authentic bevel and pure white rim
                    blend_t = (center_r - 0.94) / (0.965 - 0.94)
                    fg_rgb[y, x] = (1.0 - blend_t) * arr_exact[y, x] + blend_t * pure_white_rim
                else:
                    # Solid pure clean white rim
                    fg_rgb[y, x] = pure_white_rim
            else:
                # Outside the button
                if cov == 0.0:
                    fg_rgb[y, x] = 0.0
    
    master_rgba = np.zeros((H, W, 4), dtype=np.uint8)
    master_rgba[:, :, :3] = np.clip(fg_rgb, 0, 255).astype(np.uint8)
    master_rgba[:, :, 3] = np.clip(alpha * 255.0, 0, 255).astype(np.uint8)
    
    return master_rgba, alpha

def update_all_nav_drawables(master_rgba, alpha):
    print("Updating all 5 nav drawables with clean, smudge-free QRIS dome...")
    
    # 1. Home Active: exactly master_rgba
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
        # Start from original drawable before our changes
        before_path = os.path.join(BEFORE_DIR, d)
        if not os.path.exists(before_path):
            before_path = os.path.join(RES_DIR, d)
            
        orig_arr = np.array(Image.open(before_path)).copy()
        
        # Replace the entire QRIS region (columns 295..423, all rows) with master_rgba
        orig_arr[:, 295:424, :] = master_rgba[:, 295:424, :]
        
        # Apply the master alpha and top boundary for y < 64 across all columns
        orig_arr[:64, :, :] = master_rgba[:64, :, :]
        
        # Ensure alpha for y >= 64 is solid 255
        orig_arr[64:, :, 3] = 255
        
        im_updated = Image.fromarray(orig_arr, mode='RGBA')
        im_updated.save(os.path.join(RES_DIR, d))
        print(f"  Saved {d}")

if __name__ == "__main__":
    backup_original_drawables()
    master_rgba, alpha = build_perfect_qris_navbar()
    update_all_nav_drawables(master_rgba, alpha)
    print("Nav drawables successfully synthesized and deployed.")
