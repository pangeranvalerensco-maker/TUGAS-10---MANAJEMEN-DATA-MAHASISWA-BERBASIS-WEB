# 🏆 Aplikasi Manajemen Data Mahasiswa Berbasis Web (CRUD)

Aplikasi ini merupakan sistem manajemen data mahasiswa yang menerapkan konsep **CRUD (Create, Read, Update, Delete)** secara lengkap. Proyek dibangun menggunakan arsitektur **Java Spring Boot Layering (Controller → Service → Repository)**, **MySQL**, dan **Thymeleaf** sebagai Template Engine.

Tujuan proyek ini adalah untuk memberikan pemahaman bagaimana data diproses dari request pengguna, diproses melalui logika bisnis, dan disimpan ke database dengan struktur yang rapi dan terpisah.

---

## ⭐ Fitur Utama

| Kategori | Fitur | Status |
| :--- | :--- | :--- |
| **Wajib** | CRUD Mahasiswa (Tambah, Lihat, Ubah, Hapus) | ✅ Implemented |
| **Arsitektur** | Spring Boot Layering (Controller–Service–Repository) | ✅ Implemented |
| **Validasi** | Email unik, wajib diisi, format email (@), validasi tahun masuk | ✅ Implemented |
| **Nilai Tambah** | Search (nama), Filter (jurusan), Pagination (5 data/halaman) | ✅ Implemented |
| **UI/UX** | Notifikasi sukses/gagal + Tampilan stabil (CSS murni) | ✅ Implemented |

---

## 🛠️ Teknologi yang Digunakan

- **Framework:** Java Spring Boot  
- **Database:** MySQL  
- **ORM:** Spring Data JPA (Hibernate)  
- **Template Engine:** Thymeleaf  
- **Bahasa:** Java  
- **Build Tool:** Maven  

---

## 🔄 Alur Program (Cara Membaca Proses Kerja Aplikasi)

Berikut alur lengkap bagaimana aplikasi bekerja saat memproses data mahasiswa:

### 1. **User Mengakses Halaman Web**
Pengguna membuka halaman:
- Daftar mahasiswa  
- Tambah data  
- Edit data  
- Hapus data  

### 2. **Request Masuk ke Controller**
Controller menerima request HTTP GET/POST, misalnya:
- `/students`
- `/students/add`
- `/students/edit/{id}`

### 3. **Controller Meneruskan ke Service**
Controller tidak berisi logika berat.  
Ia meneruskan tugas ke **Service**:
<!-- ```java -->
studentService.getAllStudents();

### 4. **Service Memproses Logika Bisnis**
Service bertugas memproses semua aturan/validasi sebelum data disimpan ke database, seperti:

- Validasi email unik  
- Validasi input tidak kosong  
- Validasi format email harus mengandung (@)  
- Validasi tahun masuk harus angka dan sesuai aturan  
- Memproses fitur Search, Filter, dan Pagination  

Jika ada kesalahan, Service akan mengembalikan pesan error yang akan ditampilkan ke user.

### 5. **Service Memanggil Repository**
Service berbicara dengan database melalui Repository.  
Repository memiliki fungsi bawaan seperti:
- `findAll()`
- `save()`
- `deleteById()`
- `findByNameContaining()`
- `findByJurusan()`

### 6. **Repository Berinteraksi dengan Database**
Repository mengirimkan query otomatis menggunakan Hibernate (JPA) untuk:
- Menambah data mahasiswa  
- Mengubah data  
- Menghapus data  
- Mengambil data dengan filter atau search  

Semua interaksi database ada di layer Repository sehingga arsitektur tetap bersih dan terstruktur.

### 7. **Controller Mengirimkan Data ke Thymeleaf**
Setelah menerima hasil dari Service, Controller mengirimkan data ke Template Engine (HTML) menggunakan:
<!-- ```java -->
model.addAttribute("students", listData);
return "students-list";

### 8. **Thymeleaf Menampilkan Data ke User**
Thymeleaf merender halaman HTML dan menampilkan seluruh data serta interaksi yang diperlukan oleh pengguna, seperti:

- Tabel daftar mahasiswa  
- Form tambah dan edit mahasiswa  
- Pesan notifikasi sukses atau gagal  
- Navigasi pagination  
- Hasil pencarian (search)  
- Hasil filter berdasarkan jurusan  

Semua logika untuk menampilkan data dilakukan di layer view (HTML + Thymeleaf).

---

## ▶️ Demo Video YouTube

Untuk penjelasan lebih lengkap mengenai cara kerja aplikasi dan demonya:

👉 **YouTube:**  
https://www.youtube.com/@pangeranvalerensco9928

---
