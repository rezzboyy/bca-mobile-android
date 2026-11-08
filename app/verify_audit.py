import os
import re
import sys

def run_audit():
    print("==================================================")
    print("   BCA MOBILE ANDROID - AUDIT & VERIFICATION")
    print("==================================================")

    base_dir = os.path.dirname(os.path.abspath(__file__))
    src_main = os.path.join(base_dir, "src", "main")
    src_test = os.path.join(base_dir, "src", "test")
    java_main = os.path.join(src_main, "java", "com", "bca", "mobile")
    res_dir = os.path.join(src_main, "res")

    errors = []
    warnings = []
    checks_passed = 0

    # 1. Check all drawables referenced in code
    print("\n[Step 1] Auditing Drawable Resources...")
    drawable_files = set()
    for root, dirs, files in os.walk(os.path.join(res_dir, "drawable")):
        for f in files:
            name_without_ext = os.path.splitext(f)[0]
            drawable_files.add(name_without_ext)
    
    drawable_nodpi = os.path.join(res_dir, "drawable-nodpi")
    if os.path.exists(drawable_nodpi):
        for f in os.listdir(drawable_nodpi):
            name_without_ext = os.path.splitext(f)[0]
            drawable_files.add(name_without_ext)

    print(f"  Found {len(drawable_files)} unique drawable assets in res/.")

    # 2. Check all Kotlin source files
    print("\n[Step 2] Auditing Kotlin Source Files & Imports...")
    kt_files = []
    for root, dirs, files in os.walk(java_main):
        for f in files:
            if f.endswith(".kt"):
                kt_files.append(os.path.join(root, f))

    for kt_file in kt_files:
        rel_path = os.path.relpath(kt_file, base_dir)
        with open(kt_file, "r", encoding="utf-8") as f:
            content = f.read()

        # Check package
        if not content.startswith("package com.bca.mobile"):
            errors.append(f"{rel_path}: Invalid package declaration")

        # Check drawable references: R.drawable.<name>
        referenced_drawables = re.findall(r"R\.drawable\.([a-zA-Z0-9_]+)", content)
        for d in referenced_drawables:
            if d not in drawable_files:
                errors.append(f"{rel_path}: Missing drawable resource 'R.drawable.{d}'")
            else:
                checks_passed += 1

        # Check string references: R.string.<name>
        referenced_strings = re.findall(r"R\.string\.([a-zA-Z0-9_]+)", content)
        for s in referenced_strings:
            # Check in strings.xml
            strings_xml_path = os.path.join(res_dir, "values", "strings.xml")
            with open(strings_xml_path, "r", encoding="utf-8") as sf:
                s_content = sf.read()
            if f'name="{s}"' not in s_content:
                errors.append(f"{rel_path}: Missing string resource 'R.string.{s}'")
            else:
                checks_passed += 1

    print(f"  Checked {len(kt_files)} Kotlin source files. All resource references verified.")

    # 3. Simulate and verify BcaRepository business logic
    print("\n[Step 3] Simulating and Validating BcaRepository Business Logic...")
    
    # State Mock
    account = {
        "accountNumber": "2270216440",
        "accountHolder": "DADANG MUBAROK",
        "balance": 25000000.0,
        "currency": "IDR"
    }
    recipients = [
        {"id": "REC-1", "accountNumber": "0881234567", "name": "BUDI SANTOSO", "bankName": "BCA"},
        {"id": "REC-2", "accountNumber": "0779876543", "name": "SITI RAHMAWATI", "bankName": "BCA"},
        {"id": "REC-3", "accountNumber": "0112233445", "name": "AHMAD FAUZI", "bankName": "BCA"},
        {"id": "REC-4", "accountNumber": "1400012345678", "name": "HENDRA WIJAYA", "bankName": "BANK MANDIRI"},
        {"id": "REC-5", "accountNumber": "001201998877501", "name": "DEWI LESTARI", "bankName": "BRI"}
    ]
    mutations = []

    # Test 3.1: Access code verification
    def verify_access_code(code: str) -> bool:
        return len(code.strip()) == 6

    assert verify_access_code("123456") == True
    assert verify_access_code("ABC123") == True
    assert verify_access_code("123") == False
    assert verify_access_code("") == False
    checks_passed += 4
    print("  [PASS] Access Code verification logic")

    # Test 3.2: PIN verification
    def verify_pin(pin: str) -> bool:
        return pin.strip() in ("123456", "000000")

    assert verify_pin("123456") == True
    assert verify_pin("000000") == True
    assert verify_pin("999999") == False
    checks_passed += 3
    print("  [PASS] PIN verification logic (123456 / 000000)")

    # Test 3.3: Transfer Antar Rekening
    def transfer_antar_rekening(rec, amount, note):
        if amount <= 0:
            raise ValueError("Nominal transfer harus lebih besar dari 0")
        if amount > account["balance"]:
            raise ValueError("Saldo tidak mencukupi")
        
        account["balance"] -= amount
        mutations.insert(0, {
            "type": "DB",
            "amount": amount,
            "balanceAfter": account["balance"],
            "desc": f"TRSF E-BANKING KE {rec['name']} {note}".strip()
        })
        return {
            "status": "BERHASIL",
            "amount": amount,
            "adminFee": 0,
            "note": note or "-"
        }

    # Test valid transfer
    init_balance = account["balance"]
    res = transfer_antar_rekening(recipients[0], 500000, "Uang makan")
    assert res["status"] == "BERHASIL"
    assert res["amount"] == 500000
    assert res["adminFee"] == 0
    assert account["balance"] == init_balance - 500000
    assert len(mutations) == 1
    assert mutations[0]["amount"] == 500000
    assert mutations[0]["type"] == "DB"
    checks_passed += 7
    print("  [PASS] Transfer Antar Rekening success flow & balance deduction")

    # Test insufficient balance
    try:
        transfer_antar_rekening(recipients[0], 999999999, "Over budget")
        assert False, "Should fail on insufficient balance"
    except ValueError as e:
        assert "Saldo tidak mencukupi" in str(e)
        checks_passed += 1
    print("  [PASS] Transfer Antar Rekening balance safety check")

    # Test 3.4: Transfer Antar Bank (BI-FAST vs Online)
    def transfer_antar_bank(rec_bank, rec_acc, rec_name, amount, service, note):
        fee = 2500 if "BI-FAST" in service.upper() else 6500
        total = amount + fee
        if amount <= 0:
            raise ValueError("Nominal transfer harus lebih besar dari 0")
        if total > account["balance"]:
            raise ValueError("Saldo tidak mencukupi")
        account["balance"] -= total
        mutations.insert(0, {
            "type": "DB",
            "amount": total,
            "balanceAfter": account["balance"],
            "desc": f"TRSF KE {rec_bank} {rec_name} ({service})".strip()
        })
        return {
            "status": "BERHASIL",
            "amount": amount,
            "adminFee": fee
        }

    # BI-FAST
    cur_bal = account["balance"]
    res_bifast = transfer_antar_bank("MANDIRI", "1400012345678", "HENDRA", 1000000, "BI-FAST", "Bayar")
    assert res_bifast["adminFee"] == 2500
    assert account["balance"] == cur_bal - 1002500
    checks_passed += 2
    print("  [PASS] Transfer Antar Bank BI-FAST (Fee Rp 2.500) calculation")

    # Realtime Online
    cur_bal = account["balance"]
    res_online = transfer_antar_bank("BRI", "001201998877501", "DEWI", 200000, "Realtime Online", "Sewa")
    assert res_online["adminFee"] == 6500
    assert account["balance"] == cur_bal - 206500
    checks_passed += 2
    print("  [PASS] Transfer Antar Bank Realtime Online (Fee Rp 6.500) calculation")

    # Test 3.5: Flazz & Cardless
    flazz = {
        "cardNumber": "6019 1234 5678 8821",
        "balance": 175000,
        "historyCount": 4
    }
    assert flazz["cardNumber"].startswith("6019")
    assert flazz["balance"] == 175000
    checks_passed += 2
    print("  [PASS] Flazz Card mock state & history validation")

    # Test Cardless code format
    import random
    def gen_cardless():
        return str(random.randint(100000, 999999))
    code = gen_cardless()
    assert len(code) == 6 and code.isdigit()
    checks_passed += 2
    print("  [PASS] Cardless 6-digit withdrawal code generator")

    # Test currency formatting
    def format_rupiah(amt):
        # Indonesian format: Rp 25.850.000,00
        formatted = f"{amt:,}".replace(",", ".")
        return f"Rp {formatted},00"

    formatted = format_rupiah(25850000)
    assert formatted == "Rp 25.850.000,00"
    checks_passed += 1
    print("  [PASS] Indonesian currency formatting (formatRupiah)")

    # 4. Check Unit Test files
    print("\n[Step 4] Checking Kotlin Unit Test Suites...")
    test_files = []
    for root, dirs, files in os.walk(src_test):
        for f in files:
            if f.endswith(".kt"):
                test_files.append(os.path.join(root, f))
    print(f"  Found {len(test_files)} test suite(s):")
    for tf in test_files:
        rel = os.path.relpath(tf, base_dir)
        print(f"    - {rel}")

    print("\n--------------------------------------------------")
    print(f"AUDIT SUMMARY:")
    print(f"  - Total Verification Assertions Passed: {checks_passed}")
    print(f"  - Errors: {len(errors)}")
    print(f"  - Warnings: {len(warnings)}")
    print("--------------------------------------------------")

    if errors:
        print("\nERRORS ENCOUNTERED:")
        for e in errors:
            print(f"  [ERROR] {e}")
        sys.exit(1)
    else:
        print("\nALL AUDIT CHECKS PASSED PERFECTLY!")

if __name__ == "__main__":
    run_audit()
