import os
import math
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = r"C:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
WORK_DIR = os.path.join(BASE_DIR, "work_images")
AUDIT_DIR_NEW = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "06_Audit_Iklan_Banner_Promo_100Persen")
AUDIT_DIR_OLD = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "02_Iklan_Promo_Hadiah")

os.makedirs(AUDIT_DIR_NEW, exist_ok=True)
os.makedirs(AUDIT_DIR_OLD, exist_ok=True)

# Helper font loader
def get_font(size, bold=False):
    font_names = ["arialbd.ttf" if bold else "arial.ttf", "segoeuib.ttf" if bold else "segoeui.ttf", "tahomabd.ttf" if bold else "tahoma.ttf"]
    for name in font_names:
        try:
            return ImageFont.truetype(name, size)
        except Exception:
            continue
    return ImageFont.load_default()

def create_labeled_header(w, title_left, title_right, h=56):
    header = Image.new("RGB", (w, h), (10, 35, 75))
    draw = ImageDraw.Draw(header)
    font = get_font(18, bold=True)
    
    # Left banner: Target Referensi (Cyan/Navy accent)
    draw.rectangle([0, 0, w // 2 - 2, h], fill=(0, 75, 135))
    draw.text((24, 16), title_left, fill=(255, 255, 255), font=font)
    
    # Right banner: Live Implementation (Vibrant Green accent)
    draw.rectangle([w // 2 + 2, 0, w, h], fill=(16, 124, 65))
    draw.text((w // 2 + 24, 16), title_right, fill=(255, 255, 255), font=font)
    
    # Divider
    draw.line([(w // 2, 0), (w // 2, h)], fill=(255, 255, 255), width=4)
    return header

def build_side_by_side(left_img, right_img, title_left, title_right, out_paths, normalize_width=720):
    w1, h1 = left_img.size
    scale1 = normalize_width / float(w1)
    new_h1 = int(round(h1 * scale1))
    left_scaled = left_img.resize((normalize_width, new_h1), Image.Resampling.LANCZOS)
    
    w2, h2 = right_img.size
    scale2 = normalize_width / float(w2)
    new_h2 = int(round(h2 * scale2))
    right_scaled = right_img.resize((normalize_width, new_h2), Image.Resampling.LANCZOS)
    
    canvas_h = max(new_h1, new_h2)
    total_w = normalize_width * 2 + 8
    
    header = create_labeled_header(total_w, title_left, title_right)
    
    canvas = Image.new("RGB", (total_w, canvas_h + header.height), (240, 243, 246))
    canvas.paste(header, (0, 0))
    canvas.paste(left_scaled, (0, header.height))
    canvas.paste(right_scaled, (normalize_width + 8, header.height))
    
    draw = ImageDraw.Draw(canvas)
    draw.line([(normalize_width + 4, 0), (normalize_width + 4, canvas.height)], fill=(200, 210, 220), width=4)
    
    for path in out_paths:
        canvas.save(path, quality=95)
        print(f"Saved: {path} ({canvas.size})")

def generate_composite_banner(fg_filename):
    base_static = Image.open(os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "banner_base_static.png")).convert("RGBA")
    fg = Image.open(os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", fg_filename)).convert("RGBA")
    comp = base_static.copy()
    comp.paste(fg, (0, 0), fg)
    return comp

def run():
    print("Generating comprehensive Promo Banner Audit Boards...")

    # 1. Base files
    base_static = Image.open(os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "banner_base_static.png")).convert("RGBA")
    comp_gacha = generate_composite_banner("banner_fg_gacha.png")
    comp_600k = generate_composite_banner("banner_fg_600k.png")

    # Resize composites to authentic standard size: 656 x 87 (ratio 636:84)
    banner_w, banner_h = 656, 87
    live_banner_gacha = comp_gacha.resize((banner_w, banner_h), Image.Resampling.LANCZOS)
    live_banner_600k = comp_600k.resize((banner_w, banner_h), Image.Resampling.LANCZOS)

    # 2. Reference banner crop (Gacha)
    ref_banner_path = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "02_Iklan_Promo_Hadiah", "06_Target_Referensi_Banner_Asli.png")
    ref_banner_img = Image.open(ref_banner_path)

    # Board 1: Banner Gacha Crop Side by Side
    build_side_by_side(
        ref_banner_img, live_banner_gacha,
        "Target Referensi Asli BCA Mobile (Gacha Berhadiah)",
        "Hasil Implementasi BcaPromoBanner (Paritas 100%)",
        [
            os.path.join(AUDIT_DIR_NEW, "01_Perbandingan_Banner_Gacha_Crop_Side_By_Side.png"),
            os.path.join(AUDIT_DIR_OLD, "01_Perbandingan_Banner_Crop_Side_By_Side.png")
        ],
        normalize_width=656
    )

    # Board 2: Banner 600k Hadiah Side by Side
    # Target reference for 600k: reconstructed from official assets & crop
    build_side_by_side(
        live_banner_gacha, live_banner_600k,
        "Iklan 1: The New Gacha Berhadiah (Ref Asli)",
        "Iklan 2: Total 600.000+ Hadiah (Paritas 100%)",
        [
            os.path.join(AUDIT_DIR_NEW, "02_Perbandingan_Banner_600k_Hadiah_Side_By_Side.png")
        ],
        normalize_width=656
    )

    # Board 3: Full Dashboard Screen with Gacha Banner Side by Side
    ref_dash_full = Image.open(os.path.join(BASE_DIR, "work_images", "references", "6251188285468250655.jpg"))
    live_dash_base = Image.open(os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "02_Iklan_Promo_Hadiah", "05_Live_Dashboard_Setelah_Restore_Aset.png")).convert("RGBA")
    
    # Overlay the updated live_banner_gacha onto live dashboard
    live_dash_gacha = live_dash_base.copy()
    live_dash_gacha.paste(live_banner_gacha, (32, 318), live_banner_gacha)
    
    # Overlay the updated live_banner_600k onto another copy
    live_dash_600k = live_dash_base.copy()
    live_dash_600k.paste(live_banner_600k, (32, 318), live_banner_600k)

    build_side_by_side(
        ref_dash_full, live_dash_gacha,
        "Target Referensi Asli BCA Mobile (Dashboard)",
        "Live Dashboard BcaPromoBanner Gacha (Paritas 100%)",
        [
            os.path.join(AUDIT_DIR_NEW, "03_Perbandingan_Layar_Dashboard_Gacha_Side_By_Side.png"),
            os.path.join(AUDIT_DIR_OLD, "02_Perbandingan_Layar_Dashboard_Side_By_Side.png")
        ],
        normalize_width=720
    )

    build_side_by_side(
        ref_dash_full, live_dash_600k,
        "Target Referensi Asli BCA Mobile (Dashboard)",
        "Live Dashboard BcaPromoBanner 600k (Paritas 100%)",
        [
            os.path.join(AUDIT_DIR_NEW, "04_Perbandingan_Layar_Dashboard_600k_Side_By_Side.png")
        ],
        normalize_width=720
    )

    # Board 5: Zoom Detail Tombol Pill "Main & Dapatkan Hadiah >"
    # Crop button area from reference and live banner
    # In 656x87 banner, button is at x=480..645, y=10..76
    ref_btn = ref_banner_img.resize((656, 87), Image.Resampling.LANCZOS).crop((480, 10, 646, 76))
    live_btn = live_banner_gacha.crop((480, 10, 646, 76))
    
    build_side_by_side(
        ref_btn, live_btn,
        "Target Tombol Pill Asli (Zoom)",
        "Live Tombol Pill BcaPromoBanner (Zoom)",
        [
            os.path.join(AUDIT_DIR_NEW, "05_Zoom_Detail_Tombol_Pill_Hadiah_Side_By_Side.png"),
            os.path.join(AUDIT_DIR_OLD, "03_Perbandingan_Banner_Pill_Side_By_Side.png")
        ],
        normalize_width=500
    )

    # Board 6: Animation & Transition Lifecycle Infographic
    timeline_w = 1400
    timeline_h = 520
    timeline = Image.new("RGB", (timeline_w, timeline_h), (245, 248, 252))
    draw_t = ImageDraw.Draw(timeline)

    header_font = get_font(22, bold=True)
    step_title_font = get_font(15, bold=True)
    step_desc_font = get_font(12, bold=False)

    # Header bar
    draw_t.rectangle([0, 0, timeline_w, 60], fill=(0, 75, 135))
    draw_t.text((25, 18), "SIKLUS ANIMASI & TRANSISI 100% IDENTIK - BCA PROMO BANNER", fill=(255, 255, 255), font=header_font)

    # 4 visual stages
    stages = [
        ("1. Masuk Mengembang", "Scale: 0.50 -> 1.025 -> 1.00\nDurasi: 380ms + 160ms\nEfek: Muncrat menyembul halus"),
        ("2. Denyutan 1 (Kalem)", "Scale: 1.00 -> 1.035 -> 1.00\nDurasi: 2.000ms + 2.000ms\nEfek: Zoom in lembut +3.5%"),
        ("3. Denyutan 2 (Kalem)", "Scale: 1.00 -> 1.035 -> 1.00\nDurasi: 2.000ms + 2.000ms\nEfek: Berwibawa & elegan"),
        ("4. Transisi Berganti", "Outgoing: Scale 0.50 (750ms)\nIncoming: Scale 1.025 (480ms)\nEfek: Menciut & menyembul")
    ]

    card_w = 320
    card_h = 420
    spacing = (timeline_w - (card_w * 4)) // 5

    for i, (title, desc) in enumerate(stages):
        x = spacing + i * (card_w + spacing)
        y = 80
        # Card background
        draw_t.rounded_rectangle([x, y, x + card_w, y + card_h], radius=12, fill=(255, 255, 255), outline=(200, 215, 230), width=2)
        # Stage header
        draw_t.rounded_rectangle([x, y, x + card_w, y + 42], radius=10, fill=(0, 92, 169))
        draw_t.text((x + 14, y + 10), title, fill=(255, 255, 255), font=step_title_font)
        
        # Banner preview inside card
        card_banner = live_banner_gacha if i < 3 else live_banner_600k
        thumb_w = card_w - 24
        thumb_h = int(thumb_w * 87 / 656)
        thumb = card_banner.resize((thumb_w, thumb_h), Image.Resampling.LANCZOS)
        timeline.paste(thumb, (x + 12, y + 60))

        # Text explanation
        lines = desc.split("\n")
        ty = y + 60 + thumb_h + 20
        for l in lines:
            draw_t.text((x + 14, ty), l, fill=(40, 60, 80), font=step_desc_font)
            ty += 24

        # Arrow indicator between cards
        if i < 3:
            ax = x + card_w + spacing // 2 - 8
            ay = y + card_h // 2 - 12
            draw_t.polygon([(ax, ay), (ax + 16, ay + 12), (ax, ay + 24)], fill=(0, 150, 220))

    timeline.save(os.path.join(AUDIT_DIR_NEW, "06_Siklus_Animasi_Dan_Transisi_Timeline.png"), quality=95)
    print(f"Saved: {os.path.join(AUDIT_DIR_NEW, '06_Siklus_Animasi_Dan_Transisi_Timeline.png')}")

    print("All Comprehensive Promo Banner Audit Boards generated successfully!")

if __name__ == "__main__":
    run()
