import os
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = r"c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
WORK_DIR = os.path.join(BASE_DIR, "work_images")
AUDIT_OUT_DIR = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "05_Audit_OnePlus7Pro_Paritas")
os.makedirs(AUDIT_OUT_DIR, exist_ok=True)

# Helper font loader
def get_font(size, bold=False):
    try:
        font_name = "arialbd.ttf" if bold else "arial.ttf"
        return ImageFont.truetype(font_name, size)
    except Exception:
        return ImageFont.load_default()

def create_labeled_header(w, title_left, title_right, h=50):
    header = Image.new("RGB", (w, h), (10, 35, 75))
    draw = ImageDraw.Draw(header)
    font = get_font(20, bold=True)
    
    # Left banner: Target Referensi (Cyan/Navy accent)
    draw.rectangle([0, 0, w // 2 - 2, h], fill=(0, 75, 135))
    draw.text((20, 14), title_left, fill=(255, 255, 255), font=font)
    
    # Right banner: Live OnePlus Device (Vibrant Green accent)
    draw.rectangle([w // 2 + 2, 0, w, h], fill=(16, 124, 65))
    draw.text((w // 2 + 20, 14), title_right, fill=(255, 255, 255), font=font)
    
    # Divider
    draw.line([(w // 2, 0), (w // 2, h)], fill=(255, 255, 255), width=4)
    return header

def build_side_by_side(left_img, right_img, title_left, title_right, out_path, normalize_width=720):
    w1, h1 = left_img.size
    scale1 = normalize_width / float(w1)
    new_h1 = int(h1 * scale1)
    left_scaled = left_img.resize((normalize_width, new_h1), Image.Resampling.LANCZOS)
    
    w2, h2 = right_img.size
    scale2 = normalize_width / float(w2)
    new_h2 = int(h2 * scale2)
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
    
    canvas.save(out_path, quality=95)
    print(f"Saved: {out_path} ({canvas.size})")

def run():
    print("Generating comprehensive OnePlus 7 Pro audit boards...")
    
    # 1. Antar Rekening Full Screen
    ref_antarrek = Image.open(os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "04_Antar rekening", "image.png"))
    live_antarrek = Image.open(os.path.join(WORK_DIR, "s_antarrek_live.png"))
    build_side_by_side(
        ref_antarrek, live_antarrek,
        "Target Referensi Asli BCA Mobile (Antar Rekening)",
        "Hasil Live Device (OnePlus 7 Pro - Paritas 100%)",
        os.path.join(AUDIT_OUT_DIR, "01_Perbandingan_Antar_Rekening_Full_Side_By_Side.png"),
        normalize_width=720
    )
    
    # 2. Formulir Antar Rekening Crop
    ref_form = ref_antarrek.crop((0, 210, 720, 780))
    live_form = live_antarrek.crop((0, 420, 1440, 1560))
    build_side_by_side(
        ref_form, live_form,
        "Target Formulir Asli (image.png)",
        "Live Formulir OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "02_Formulir_Transfer_Side_By_Side.png"),
        normalize_width=720
    )
    
    # 3. Dialog Konfirmasi Transfer Antar Rekening
    ref_dialog = Image.open(os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "01_Menu_Antar_Rekening", "06_Target_Referensi_Dialog_Asli.png"))
    live_dialog_full = Image.open(os.path.join(WORK_DIR, "s_antarrek_confirm.png"))
    live_dialog_crop = live_dialog_full.crop((170, 790, 1270, 2330))
    build_side_by_side(
        ref_dialog, live_dialog_crop,
        "Target Dialog Konfirmasi Asli",
        "Live Dialog OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "03_Dialog_Konfirmasi_Transfer_Side_By_Side.png"),
        normalize_width=531
    )
    
    # 4. Dialog Logout
    ref_logout = Image.open(os.path.join(WORK_DIR, "sisa_kerjaan", "potongan_elemen_ui", "crop_dialog_logout.png"))
    live_logout_full = Image.open(os.path.join(WORK_DIR, "s_logout_dialog.png"))
    live_logout_crop = live_logout_full.crop((145, 900, 1295, 2220))
    build_side_by_side(
        ref_logout, live_logout_crop,
        "Target Dialog Logout Asli (crop_dialog_logout)",
        "Live Dialog Logout OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "04_Dialog_Logout_Side_By_Side.png"),
        normalize_width=560
    )
    
    # 5. Kotak Jumlah Uang Zoomed
    ref_amount = ref_antarrek.crop((30, 530, 680, 670))
    live_amount = live_antarrek.crop((60, 1060, 1380, 1340))
    build_side_by_side(
        ref_amount, live_amount,
        "Target Kotak Jumlah Uang (Zoom)",
        "Live Kotak Jumlah Uang OnePlus (Zoom)",
        os.path.join(AUDIT_OUT_DIR, "05_Kotak_Jumlah_Uang_Zoom_Side_By_Side.png"),
        normalize_width=650
    )
    
    # 6. Top Bar Header Zoomed
    ref_topbar = ref_antarrek.crop((0, 65, 720, 215))
    live_topbar = live_antarrek.crop((0, 120, 1440, 420))
    build_side_by_side(
        ref_topbar, live_topbar,
        "Target Header Top Bar & Send (Zoom)",
        "Live Header Top Bar & Send OnePlus (Zoom)",
        os.path.join(AUDIT_OUT_DIR, "06_Top_Bar_Header_Zoom_Side_By_Side.png"),
        normalize_width=720
    )

    # 7. Menu Login Utama (Hub Screen) Full Side By Side
    ref_hub = Image.open(os.path.join(WORK_DIR, "references", "hub_ref.png"))
    live_hub = Image.open(os.path.join(WORK_DIR, "s_hub_new.png"))
    build_side_by_side(
        ref_hub, live_hub,
        "Target Referensi Asli BCA Mobile (Hub)",
        "Live Device (OnePlus 7 Pro - Paritas 100%)",
        os.path.join(AUDIT_OUT_DIR, "07_Perbandingan_Hub_Login_Utama_Side_By_Side.png"),
        normalize_width=720
    )

    # 8. Transfer Antar Bank Screen Full Side By Side
    ref_antarbank = Image.open(os.path.join(WORK_DIR, "references", "WhatsApp Image 2026-11-08 at 18.00.20 (1).jpeg"))
    live_antarbank = Image.open(os.path.join(WORK_DIR, "s_antarbank_new.png"))
    build_side_by_side(
        ref_antarbank, live_antarbank,
        "Target Referensi Asli BCA Mobile (Antar Bank)",
        "Live Device (OnePlus 7 Pro - Paritas 100%)",
        os.path.join(AUDIT_OUT_DIR, "08_Perbandingan_Antar_Bank_Side_By_Side.png"),
        normalize_width=720
    )

    # 9. Dialog Konfirmasi Transfer Antar Bank Side By Side
    ref_conf_antarbank = Image.open(os.path.join(WORK_DIR, "sisa_kerjaan", "potongan_elemen_ui", "crop_dialog_antarbank.png"))
    live_conf_antarbank = Image.open(os.path.join(WORK_DIR, "s_antarbank_confirmation.png"))
    live_conf_crop = live_conf_antarbank.crop((145, 650, 1295, 2450))
    build_side_by_side(
        ref_conf_antarbank, live_conf_crop,
        "Target Dialog Konfirmasi Asli (Antar Bank)",
        "Live Dialog OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "09_Dialog_Konfirmasi_Antar_Bank_Side_By_Side.png"),
        normalize_width=560
    )

    # 10. Formulir Antar Bank Terisi Side By Side
    ref_form_filled = Image.open(os.path.join(WORK_DIR, "sisa_kerjaan", "potongan_elemen_ui", "crop_antarbank_form_top.png"))
    live_form_filled = Image.open(os.path.join(WORK_DIR, "s_antarbank_with_amount.png"))
    live_form_crop = live_form_filled.crop((0, 480, 1440, 1350))
    build_side_by_side(
        ref_form_filled, live_form_crop,
        "Target Formulir Terisi Asli (crop_antarbank_form_top)",
        "Live Formulir Terisi OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "10_Formulir_Antar_Bank_Terisi_Side_By_Side.png"),
        normalize_width=720
    )

    # 11. Dialog Kode Akses Login Utama Side By Side
    ref_login_photo = Image.open(os.path.join(WORK_DIR, "references", "ref_084537.png"))
    ref_login_crop = ref_login_photo.crop((110, 380, 475, 575))
    live_login_img = Image.open(os.path.join(WORK_DIR, "s_login_dialog_live.png"))
    live_login_crop = live_login_img.crop((140, 680, 1300, 1370))
    build_side_by_side(
        ref_login_crop, live_login_crop,
        "Target Dialog Kode Akses Asli (ref_084537)",
        "Live Dialog Kode Akses OnePlus 7 Pro",
        os.path.join(AUDIT_OUT_DIR, "11_Dialog_Kode_Akses_Login_Side_By_Side.png"),
        normalize_width=560
    )

    print("All 11 comprehensive OnePlus 7 Pro audit boards generated successfully!")

if __name__ == "__main__":
    run()
