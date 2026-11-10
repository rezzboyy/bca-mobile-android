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

def get_font(size, bold=False):
    font_names = ["arialbd.ttf" if bold else "arial.ttf", "segoeui.ttf", "tahoma.ttf"]
    for name in font_names:
        try:
            return ImageFont.truetype(name, size)
        except Exception:
            continue
    return ImageFont.load_default()

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
    print("Synthesizing mathematically perfect QRIS dome and alpha mask...")
    im_exact = Image.open(os.path.join(WORK_DIR, "references", "bca_nav_exact.png"))
    arr_exact = np.array(im_exact).astype(float)
    
    H, W = 200, 720
    alpha = np.zeros((H, W), dtype=np.float32)
    
    # Mathematical Squircle Model (Superellipse) derived from real BCA contour
    xc, yc = 358.75, 81.0
    a, b, n = 52.0, 54.0, 2.40
    r_sh = 1.028 # drop shadow zone
    
    # 16x16 subpixel grid for analytical coverage anti-aliasing
    sub = np.linspace(0.5/16, 1.0 - 0.5/16, 16)
    sub_x, sub_y = np.meshgrid(sub, sub)
    sub_x = sub_x.flatten()
    sub_y = sub_y.flatten()
    
    # y >= 64 is solidly the flat navbar
    alpha[64:, :] = 1.0
    
    # y < 64 dome region
    for y in range(20, 64):
        for x in range(295, 425):
            xs = x + sub_x
            ys = y + sub_y
            
            sq = (np.abs(xs - xc) / a)**n + (np.abs(ys - yc) / b)**n
            r_norm = sq**(1.0 / n)
            
            inside = (r_norm <= 1.0) | (ys >= 64.0)
            in_shadow = (r_norm > 1.0) & (r_norm <= r_sh) & (ys < 64.0)
            
            sh_w = np.maximum(0.0, 1.0 - (r_norm[in_shadow] - 1.0) / (r_sh - 1.0)) * 0.20
            cov = np.sum(inside) / 256.0 + np.sum(sh_w) / 256.0
            alpha[y, x] = min(1.0, cov)
            
    # Subpixel anti-aliasing for the flat line y=63
    for x in range(W):
        if x < 306 or x > 410:
            alpha[63, x] = 0.12
            
    # Unmix foreground RGB along anti-aliased edge to prevent dark halos on light screens
    bg_navy = np.array([0.0, 34.0, 69.0])
    fg_rgb = np.copy(arr_exact)
    
    for y in range(H):
        for x in range(W):
            al = alpha[y, x]
            if 0.0 < al < 1.0 and y < 64 and 295 <= x <= 425:
                unmixed = (arr_exact[y, x] - (1.0 - al) * bg_navy) / al
                fg_rgb[y, x] = np.clip(unmixed, 0.0, 255.0)
                
    master_rgba = np.zeros((H, W, 4), dtype=np.uint8)
    master_rgba[:, :, :3] = np.clip(fg_rgb, 0, 255).astype(np.uint8)
    master_rgba[:, :, 3] = np.clip(alpha * 255.0, 0, 255).astype(np.uint8)
    
    return master_rgba, alpha

def update_all_nav_drawables(master_rgba, alpha):
    print("Updating all 5 nav drawables with flawless QRIS dome...")
    
    # 1. Home Active: exactly master_rgba
    im_home = Image.fromarray(master_rgba, mode='RGBA')
    im_home.save(os.path.join(RES_DIR, "bca_nav_bar_home.png"))
    print("  Saved bca_nav_bar_home.png")
    
    # For the other 4 drawables, retain their active/inactive tab graphics (y >= 65)
    # and replace the QRIS dome and top boundary with the master clean version
    other_drawables = [
        "bca_nav_bar_transaksi.png",
        "bca_nav_bar_notifikasi.png",
        "bca_nav_bar_akun_saya.png",
        "bca_nav_bar_all_grey.png"
    ]
    
    for d in other_drawables:
        before_path = os.path.join(BEFORE_DIR, d)
        orig_arr = np.array(Image.open(before_path)).copy()
        
        # Replace the entire QRIS region (columns 288..432, all rows) with master_rgba
        orig_arr[:, 288:432, :] = master_rgba[:, 288:432, :]
        
        # Apply the master alpha and top boundary for y < 65 across all columns
        orig_arr[:65, :, :] = master_rgba[:65, :, :]
        
        # Ensure alpha for y >= 65 is solid 255
        orig_arr[65:, :, 3] = 255
        
        im_updated = Image.fromarray(orig_arr, mode='RGBA')
        im_updated.save(os.path.join(RES_DIR, d))
        print(f"  Saved {d}")

if __name__ == "__main__":
    backup_original_drawables()
    master_rgba, alpha = build_perfect_qris_navbar()
    update_all_nav_drawables(master_rgba, alpha)
    print("Nav drawables successfully synthesized and deployed.")
