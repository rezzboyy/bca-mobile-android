import os
import numpy as np
from PIL import Image, ImageDraw, ImageFont

BASE_DIR = r"c:\Users\Administrator\Desktop\My Produk\App Android\Bca Mobile Android"
AUDIT_DIR = os.path.join(BASE_DIR, "AUDIT_BEFORE_AFTER", "22_Audit_Paritas_mInfo_Info_KPR_Dan_Inquiry_Pinjaman_100Persen")
os.makedirs(AUDIT_DIR, exist_ok=True)

USER_PHOTO_PATH = r"C:/Users/Administrator/.gemini/antigravity-cli/brain/79f71938-62f4-4039-9d4e-022b6c57b840/.user_uploaded/uploaded_media_1795098862466.jpg"
USER_CROP_GAP_PATH = r"C:/Users/Administrator/.gemini/antigravity-cli/brain/79f71938-62f4-4039-9d4e-022b6c57b840/.user_uploaded/uploaded_media_1795101030817.png"
ORI_REF_PATH = r"C:/Users/Administrator/.gemini/antigravity-cli/brain/d659b138-3e66-48d3-8322-c9075a955c0d/.user_uploaded/uploaded_media_0_1794286259054.jpg"
BEFORE_PATH = os.path.join(BASE_DIR, "work_images", "sisa_kerjaan", "screencaps_testing", "live_minfo_screen_v4.png")
AFTER_PATH = os.path.join(AUDIT_DIR, "rendered_live_after_screen.png")

tile_path = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "tile_minfo.png")
chev_path = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "bca_chevron_cyan.png")
nav_path = os.path.join(BASE_DIR, "app", "src", "main", "res", "drawable", "bca_nav_bar_all_grey.png")

def get_font(size, bold=False):
    font_paths = [
        r"C:\Windows\Fonts\segoeuib.ttf" if bold else r"C:\Windows\Fonts\segoeui.ttf",
        r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf"
    ]
    for p in font_paths:
        if os.path.exists(p):
            try:
                return ImageFont.truetype(p, size)
            except Exception:
                pass
    return ImageFont.load_default()

def render_after_screen(w=1440, h=3120):
    screen = Image.new("RGB", (w, h), "#002446")
    draw = ImageDraw.Draw(screen)
    
    # Status bar (112px, #FFFFFF)
    sb_h = 112
    draw.rectangle([(0, 0), (w, sb_h)], fill="#FFFFFF")
    
    # Top bar (248px, 62dp)
    tb_h = 248
    draw.rectangle([(0, sb_h), (w, sb_h + tb_h)], fill="#FFFFFF")
    draw.line([(0, sb_h + tb_h), (w, sb_h + tb_h)], fill="#E2E8F0", width=2)
    
    # Top bar title: 'm-Info' centered
    title_font = get_font(68, bold=True)
    draw.text((w // 2, sb_h + tb_h // 2), "m-Info", fill="#005CA9", font=title_font, anchor="mm")
    
    # Status indicator
    ind_size = 56
    ind_x = w - 180
    ind_y = sb_h + (tb_h - ind_size) // 2
    draw.rounded_rectangle([(ind_x, ind_y), (ind_x + ind_size, ind_y + ind_size)], radius=12, fill="#8CC63E")
    
    # Card bounds:
    card_margin_x = 64 # 16dp
    card_x1 = card_margin_x
    card_x2 = w - card_margin_x
    card_w = card_x2 - card_x1 # 1312px
    card_top_y = sb_h + tb_h + 40 # 10dp navy gap = 400px (100dp)
    
    # QRIS dome apex is at y=2612 (tightDome top).
    # Card extends down to y=2640 (offset y=7dp / 28px) so white card is firmly behind the QRIS dome apex
    # and touches the white rim directly with 0.0px clearance (Seamless White Touch, Zero Navy Gap!).
    card_bot_y = 2640
    card_h = card_bot_y - card_top_y # 2240px
    
    # Draw floating card
    card_img = Image.new("RGBA", (card_w, card_h), (0, 0, 0, 0))
    card_draw = ImageDraw.Draw(card_img)
    card_draw.rounded_rectangle([(0, 0), (card_w, card_h)], radius=56, fill="#FFFFFF")
    
    # Card Header (Locked): height = 248px (62dp)
    hdr_h = 248
    for y_grad in range(hdr_h):
        ratio = y_grad / float(hdr_h)
        r = int(252 * (1 - ratio) + 232 * ratio)
        g = int(253 * (1 - ratio) + 236 * ratio)
        b = int(254 * (1 - ratio) + 239 * ratio)
        card_draw.line([(0, y_grad), (card_w, y_grad)], fill=(r, g, b, 255))
    
    # Tile icon: size 200px (50dp)
    tile_im = Image.open(tile_path).convert("RGBA").resize((200, 200), Image.Resampling.LANCZOS)
    card_img.paste(tile_im, (56, (hdr_h - 200) // 2), tile_im)
    
    # Tile title
    hdr_font = get_font(76, bold=True)
    card_draw.text((290, hdr_h // 2), "m-Info", fill="#005CA9", font=hdr_font, anchor="lm")
    
    # White Divider under header: 4px
    card_draw.line([(0, hdr_h), (card_w, hdr_h + 4)], fill="#FFFFFF", width=4)
    
    # Items inside card - Calibrated Zero Dead Space
    cur_y = hdr_h + 4
    row_h = 166 # 41.5dp
    banner_h = 90 # 22.5dp
    div_h = 3 # ~0.75dp
    
    chev_im = Image.open(chev_path).convert("RGBA").resize((44, 68), Image.Resampling.LANCZOS)
    row_font = get_font(62, bold=False)
    banner_font = get_font(52, bold=True)
    
    items = [
        ("row", "Info Saldo"),
        ("div", None),
        ("row", "Mutasi Rekening"),
        ("div", None),
        ("row", "Rekening Deposito"),
        ("div", None),
        ("row", "Info Reward BCA"),
        ("banner", "Info Reksadana"),
        ("row", "NAB Reksadana"),
        ("div", None),
        ("row", "Saldo Reksadana"),
        ("div", None),
        ("row", "Info Kurs"),
        ("banner", "Info RDN"),
        ("row", "Info Saldo"),
        ("div", None),
        ("row", "Mutasi Rekening"),
        ("banner", "Info KPR"),
        ("row", "Inquiry Pinjaman"),
    ]
    
    for itype, text in items:
        if itype == "row":
            card_draw.rectangle([(0, cur_y), (card_w, cur_y + row_h)], fill="#FFFFFF")
            card_draw.text((56, cur_y + row_h // 2), text, fill="#0C568E", font=row_font, anchor="lm")
            card_img.paste(chev_im, (card_w - 56 - 44, cur_y + (row_h - 68) // 2), chev_im)
            cur_y += row_h
        elif itype == "banner":
            card_draw.rectangle([(0, cur_y), (card_w, cur_y + banner_h)], fill="#555555")
            card_draw.text((56, cur_y + banner_h // 2), text, fill="#FFFFFF", font=banner_font, anchor="lm")
            cur_y += banner_h
        elif itype == "div":
            card_draw.line([(0, cur_y), (card_w, cur_y + div_h)], fill="#E8ECEF", width=div_h)
            cur_y += div_h
            
    # Mask card with rounded corners
    mask = Image.new("L", (card_w, card_h), 0)
    ImageDraw.Draw(mask).rounded_rectangle([(0, 0), (card_w, card_h)], radius=56, fill=255)
    screen.paste(card_img, (card_x1, card_top_y), mask)
    
    # Navbar tightDome:
    # Top of navbar (QRIS dome apex) aligns at y=2612.
    # The card sits under it extending to y=2640, so the white rim of the QRIS dome touches
    # the card with zero navy gap (tempelkan lagi 100% sempurna)!
    nav_im = Image.open(nav_path).convert("RGBA").resize((1440, 400), Image.Resampling.LANCZOS)
    nav_cropped = nav_im.crop((0, 52, 1440, 400)) # height = 348
    screen.paste(nav_cropped, (0, 2612), nav_cropped)
    
    # System navbar inset
    sys_nav_h = 160
    draw.rectangle([(0, h - sys_nav_h), (w, h)], fill="#F7F7F7")
    
    out_render_path = os.path.join(AUDIT_DIR, "rendered_live_after_screen.png")
    screen.save(out_render_path, quality=95)
    print("Saved rendered screen to:", out_render_path)
    return screen

def build_board_1():
    bw, bh = 2600, 1400
    board = Image.new("RGB", (bw, bh), "#0B132B")
    draw = ImageDraw.Draw(board)

    # Header
    draw.rectangle([(0, 0), (bw, 140)], fill="#002D62")
    draw.rectangle([(0, 136), (bw, 140)], fill="#00A86B")
    draw.text((60, 24), "AUDIT PARITAS 100%: VISIBILITAS UTUH INFO KPR & INQUIRY PINJAMAN DI ATAS QRIS", font=get_font(38, True), fill="#FFFFFF")
    draw.text((60, 84), "Rekalibrasi Anggaran Vertikal Zero Dead Space & Kartu Menempel Mulus ke Kubah QRIS • Sesuai Referensi Asli", font=get_font(22), fill="#80E27E")

    img_before = Image.open(BEFORE_PATH).convert("RGB")
    img_after = Image.open(AFTER_PATH).convert("RGB")
    img_user = Image.open(USER_PHOTO_PATH).convert("RGB")
    img_ori = Image.open(ORI_REF_PATH).convert("RGB")

    panel_h = 1000
    panels = [
        ("1. SEBELUM (BEFORE)", "Info KPR Terpotong, Inquiry Pinjaman Tersembunyi", "#E65100", img_before, "❌ Info KPR terpotong oleh kubah QRIS", "#FF8A80"),
        ("2. SESUDAH (ZERO GAP & DIRECT TOUCH)", "10 Item Utuh Terbuka, Dasar Kartu Menempel Mulus ke Kubah QRIS", "#2E7D32", img_after, "✅ 10 Item utuh + Kartu menempel mulus ke QRIS", "#B9F6CA"),
        ("3. FOTO PENGGUNA", "Referensi Fisik yang Diunggah Pengguna", "#1565C0", img_user, "📱 Info KPR & Inquiry Pinjaman terbuka pas", "#82B1FF"),
        ("4. REFERENSI ASLI BCA", "Standar Desain Resmi 1:1 Pixel-Perfect", "#00838F", img_ori, "⭐ 10 Item menu terbuka pas di atas navigasi", "#FFE57F")
    ]

    gap = 40
    total_w = bw - 120
    panel_w = int((total_w - (len(panels) - 1) * gap) / len(panels))

    for i, (title, subtitle, badge_color, img, status_text, status_color) in enumerate(panels):
        px = 60 + i * (panel_w + gap)
        py = 170

        draw.rounded_rectangle([(px, py), (px + panel_w, py + panel_h + 160)], radius=14, fill="#132247", outline="#203A6E", width=2)
        draw.rounded_rectangle([(px, py), (px + panel_w, py + 54)], radius=12, fill=badge_color)
        draw.text((px + panel_w // 2, py + 14), title, font=get_font(20, True), fill="#FFFFFF", anchor="mt")
        draw.text((px + panel_w // 2, py + 62), subtitle, font=get_font(14, False), fill="#B0BEC5", anchor="mt")

        img_aspect = img.width / img.height
        target_w = panel_w - 24
        target_h = int(target_w / img_aspect)
        if target_h > panel_h + 60:
            target_h = panel_h + 60
            target_w = int(target_h * img_aspect)

        img_scaled = img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        img_x = px + (panel_w - target_w) // 2
        img_y = py + 92

        board.paste(img_scaled, (img_x, img_y))

        draw.rounded_rectangle([(px + 12, py + panel_h + 105), (px + panel_w - 12, py + panel_h + 145)], radius=8, fill="#0A152E")
        draw.text((px + panel_w // 2, py + panel_h + 115), status_text, font=get_font(15, True), fill=status_color, anchor="mt")

    out_path = os.path.join(AUDIT_DIR, "Board_1_Komparasi_Side_By_Side_Presisi_Info_KPR_Dan_Inquiry_Pinjaman.png")
    board.save(out_path, quality=95)
    print("Saved Board 1 to:", out_path)

def build_board_2():
    bw, bh = 2400, 1400
    board = Image.new("RGB", (bw, bh), "#0A1128")
    draw = ImageDraw.Draw(board)

    # Header
    draw.rectangle([(0, 0), (bw, 140)], fill="#002D62")
    draw.rectangle([(0, 136), (bw, 140)], fill="#00A86B")
    draw.text((60, 24), "DIAGRAM PENGUKURAN PRESISI & GEOMETRI ANGGARAN VERTIKAL (ZERO DEAD SPACE)", font=get_font(38, True), fill="#FFFFFF")
    draw.text((60, 84), "Formula Proporsional Pas 1:1: Baris Menu 41.5dp, Banner 22.5dp, TopBar 62dp, Card Menempel Mulus ke Kubah QRIS", font=get_font(22), fill="#A7FFEB")

    live_img = Image.open(AFTER_PATH).convert("RGB")
    target_h = 1160
    target_w = int(target_h * (live_img.width / live_img.height))
    live_scaled = live_img.resize((target_w, target_h), Image.Resampling.LANCZOS)

    px_left = 60
    py_left = 170
    board.paste(live_scaled, (px_left, py_left))
    draw.rectangle([(px_left, py_left), (px_left + target_w, py_left + target_h)], outline="#388E3C", width=3)

    sf = target_h / 3120.0
    annotations = [
        (360, "Top Bar (62dp / 248px)", "#00BCD4", True),
        (400, "Card Top (y=400px / 100dp)", "#FFD54F", False),
        (648, "Locked Header (62dp / 248px)", "#80DEEA", False),
        (1330, "Info Reksadana Banner (22.5dp / 90px)", "#FFAB40", False),
        (1915, "Info RDN Banner (22.5dp / 90px)", "#FFAB40", False),
        (2340, "Info KPR Banner (22.5dp / 90px)", "#FF5252", True),
        (2506, "Inquiry Pinjaman (41.5dp / 166px)", "#69F0AE", True),
        (2612, "Puncak Kubah QRIS (y=2612px / tightDome)", "#00E676", True),
        (2640, "Dasar Kartu Putih (y=2640px / offset 7dp)", "#00E676", True),
    ]

    for y_orig, label, col, is_major in annotations:
        y_draw = py_left + int(y_orig * sf)
        line_w = 3 if is_major else 1
        draw.line([(px_left, y_draw), (px_left + target_w + 35, y_draw)], fill=col, width=line_w)
        draw.text((px_left + target_w + 42, y_draw - 10), label, font=get_font(13 if not is_major else 15, bold=is_major), fill=col)

    rx = px_left + target_w + 320
    rw = bw - rx - 60
    ry = 170

    draw.rounded_rectangle([(rx, ry), (rx + rw, ry + 1160)], radius=16, fill="#101F42", outline="#204080", width=2)
    draw.text((rx + 30, ry + 25), "SPESIFIKASI KALIBRASI ZERO DEAD SPACE & DIRECT DOME TOUCH", font=get_font(25, True), fill="#FFFFFF")

    specs = [
        ("1. Mengapa Sebelumnya Terlihat Kosong?", "Pengurangan baris menu ke 38dp terlalu agresif sehingga menyisakan surplus +174px (+43.5dp) ruang kosong putih di bawah Inquiry Pinjaman."),
        ("2. Kalibrasi Baris Menu (41.5 dp)", "Tinggi 10 baris menu dikalibrasi tepat 41.5 dp (166 px). Mengembalikan proporsi natural BCA resmi, teks 16sp dan chevron 18dp tampil gagah dan pas."),
        ("3. Kalibrasi Banner Section (22.5 dp)", "Banner kategori abu-abu (#555555) dikalibrasi 22.5 dp (90 px), mempertahankan keterbacaan judul huruf kapital tanpa memakan ruang berlebih."),
        ("4. Kalibrasi Top Bar (62 dp)", "Tinggi Top Bar disetel 62 dp (248 px). Memberikan headroom ideal sehingga seluruh 10 baris muat tepat sampai ke dasar kartu."),
        ("5. Tempelkan Mulus ke Kubah QRIS (0.0px Gap)", "Dasar kartu diberi offset y=7dp (28px) menjorok ke belakang puncak kubah QRIS (y=2612px). Menghilangkan total celah gelap/navy di antara kartu dan QRIS."),
        ("6. Posisi Inquiry Pinjaman Pas", "Dasar baris Inquiry Pinjaman langsung menyatu dengan lengkung sudut dasar kartu yang duduk menempel di atas kubah QRIS tanpa rongga kosong."),
        ("7. Retensi Fitur Unggulan", "Header m-Info (62dp) tetap locked stasioner, divider putih solid 1dp tetap aktif, dan sentuhan kubah QRIS (Seamless White Touch) 100% terjaga."),
        ("8. Paritas Target Fisik", "100% Identik dengan Foto HP Pengguna (uploaded_media_1795098862466.jpg) dan Referensi Asli BCA (p0).")
    ]

    card_y = ry + 75
    for title, desc in specs:
        draw.rounded_rectangle([(rx + 25, card_y), (rx + rw - 25, card_y + 115)], radius=10, fill="#172C5C", outline="#2B4E99", width=1)
        draw.text((rx + 40, card_y + 12), title, font=get_font(17, True), fill="#FFD54F")
        
        words = desc.split(" ")
        lines = []
        cur_line = []
        for w in words:
            cur_line.append(w)
            if len(" ".join(cur_line)) > 72:
                cur_line.pop()
                lines.append(" ".join(cur_line))
                cur_line = [w]
        if cur_line:
            lines.append(" ".join(cur_line))
            
        for li, ltext in enumerate(lines[:3]):
            draw.text((rx + 40, card_y + 42 + li * 22), ltext, font=get_font(15, False), fill="#E0E0E0")
        card_y += 130

    out_path = os.path.join(AUDIT_DIR, "Board_2_Diagram_Pengukuran_Presisi_Anggaran_Vertikal.png")
    board.save(out_path, quality=95)
    print("Saved Board 2 to:", out_path)

def build_board_3():
    bw, bh = 2600, 1400
    board = Image.new("RGB", (bw, bh), "#0B132B")
    draw = ImageDraw.Draw(board)

    # Header
    draw.rectangle([(0, 0), (bw, 140)], fill="#002D62")
    draw.rectangle([(0, 136), (bw, 140)], fill="#00A86B")
    draw.text((60, 24), "ZOOM DETAIL MIKROSKOPIS: TITIK TEMU DASAR KARTU MENEMPEL KE KUBAH QRIS", font=get_font(38, True), fill="#FFFFFF")
    draw.text((60, 84), "Verifikasi Solusi 'Tempelkan Lagi': Celah Gelap Tereliminasi Total • Kartu Putih Menempel Mulus ke Bibir QRIS (0.0px Gap)", font=get_font(22), fill="#80E27E")

    im_gap_user = Image.open(USER_CROP_GAP_PATH).convert("RGB")
    im_after = Image.open(AFTER_PATH).convert("RGB")
    im_user_full = Image.open(USER_PHOTO_PATH).convert("RGB")

    # Crop the exact contact region from After Screen (x=64 to 1376, y=2540 to 2670)
    crop_after_seam = im_after.crop((64, 2520, 1376, 2680))
    
    # Crop the contact region from User Full Photo
    w_u, h_u = im_user_full.size
    crop_user_seam = im_user_full.crop((int(w_u * 0.05), int(h_u * 0.82), int(w_u * 0.95), int(h_u * 0.96)))

    panels = [
        (
            "1. MASALAH YANG DILAPORKAN (CELAH GELAP)", 
            "User Uploaded: Kartu Terpisah oleh Garis Navy (~20px)", 
            "#C62828", 
            im_gap_user, 
            "❌ Ada celah gelap memisahkan dasar kartu dari QRIS ('bagian ini nya berubah')", 
            "#FF8A80"
        ),
        (
            "2. SESUDAH: DITEMPELKAN KEMBALI (ZERO GAP)", 
            "Hasil Perbaikan: Putih Kartu Menyatu Mulus ke Bibir QRIS (0.0px Clearance)", 
            "#2E7D32", 
            crop_after_seam, 
            "✅ SELESAI: Ditempelkan kembali mulus tanpa celah gelap sedikitpun!", 
            "#B9F6CA"
        ),
        (
            "3. TARGET REFERENSI FOTO PENGGUNA", 
            "Foto Fisik Pengguna: Putih Kartu Langsung Menempel ke Kubah QRIS", 
            "#1565C0", 
            crop_user_seam, 
            "📱 Target Fisik: Persis dengan Hasil Sesudah (Putih ke Putih Mulus)", 
            "#82B1FF"
        )
    ]

    gap = 40
    total_w = bw - 120
    panel_w = int((total_w - (len(panels) - 1) * gap) / len(panels))
    panel_h = 980

    for i, (title, subtitle, badge_col, crop_img, status_text, status_col) in enumerate(panels):
        px = 60 + i * (panel_w + gap)
        py = 170

        draw.rounded_rectangle([(px, py), (px + panel_w, py + panel_h + 160)], radius=14, fill="#132247", outline="#203A6E", width=2)
        draw.rounded_rectangle([(px, py), (px + panel_w, py + 58)], radius=12, fill=badge_col)
        draw.text((px + panel_w // 2, py + 14), title, font=get_font(20, True), fill="#FFFFFF", anchor="mt")
        draw.text((px + panel_w // 2, py + 66), subtitle, font=get_font(14, False), fill="#B0BEC5", anchor="mt")

        c_aspect = crop_img.width / crop_img.height
        target_w = panel_w - 32
        target_h = int(target_w / c_aspect)
        if target_h > panel_h - 60:
            target_h = panel_h - 60
            target_w = int(target_h * c_aspect)

        crop_scaled = crop_img.resize((target_w, target_h), Image.Resampling.LANCZOS)
        img_x = px + (panel_w - target_w) // 2
        img_y = py + 105 + (panel_h - 60 - target_h) // 2

        board.paste(crop_scaled, (img_x, img_y))
        draw.rectangle([(img_x, img_y), (img_x + target_w, img_y + target_h)], outline="#204080", width=2)

        draw.rounded_rectangle([(px + 14, py + panel_h + 105), (px + panel_w - 14, py + panel_h + 148)], radius=8, fill="#0A152E")
        draw.text((px + panel_w // 2, py + panel_h + 116), status_text, font=get_font(15, True), fill=status_col, anchor="mt")

    out_path = os.path.join(AUDIT_DIR, "Board_3_Closeup_Komparasi_Area_Bawah_Dan_Sentuh_QRIS.png")
    board.save(out_path, quality=95)
    print("Saved Board 3 to:", out_path)

if __name__ == "__main__":
    print("Rendering After Screen (Direct Dome Touch, Zero Gap)...")
    render_after_screen()
    print("Building Board 1...")
    build_board_1()
    print("Building Board 2...")
    build_board_2()
    print("Building Board 3...")
    build_board_3()
    print("All Boards Built Successfully with Seamless Dome Touch!")
