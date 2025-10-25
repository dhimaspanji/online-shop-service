# 🛒 Online Shop Service (Spring Boot)

Sebuah project sederhana untuk simulasi manajemen **item**, **inventory (stok)**, dan **orders (transaksi penjualan)** berbasis **Spring Boot**, dengan dukungan:
- Error handling custom (format `errorCode`, `errorDesc`, `timestamp`)
- Full unit test coverage (100%) untuk seluruh service layer
- Struktur modular & bersih (service, repository, exception, utils)
- Pagination bawaan Spring Data

---

## ⚙️ Tech Stack
- **Java 17+**
- **Spring Boot 3.x**
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
- **H2 Database** (in-memory)
- **Lombok**
- **Apache Commons Lang** (`StringUtils`)
- **JUnit 5 + Mockito**
- **JaCoCo / IntelliJ coverage**

