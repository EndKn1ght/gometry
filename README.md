## Capstone Project

![Screenshot](app-banner.webp)
**GOMetry** adalah aplikasi pembelajaran bangun ruang interaktif dengan menggunakan teknologi **Augmented Reality** dan konsep **Gamifikasi**. Proyek ini dibuat oleh **Tim C22-218** untuk memenuhi proyek akhir SIB Dicoding Batch 3. Berikut adalah daftar anggota **Tim C22-218**:
  - Furqon Al-Rasyid
  - Herdi Herdianurdin
  - Rizky Akbar Ramadhan
  - Ahmad Usamah Ali

### Struktur JSON Realtime Database
```JSON
{
  "users": {
    "userId": {
      "achievements": ["beginner", "skilled", "proficient"],
      "displayName": "John Doe",
      "email": "john@mail.com",
      "geometries": ["cube", "beam", "triangular_pyramid", "ball", "cone", "tube", "triangular_prism"],
      "id": "userId",
      "photoUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/John_and_Jane_Doe_Headstones.jpg/1200px-John_and_Jane_Doe_Headstones.jpg",
      "point": 700
    }
  },
  "questions": {
    "questionId": {
      "anwer": "Kubus",
      "geometryId": "cube",
      "image": "cube-1.webp",
      "level": "beginner",
      "option": ["Balok", "Prisma", "Tabung", "Kubus"],
      "question": "Apa nama bangun ruang tersebut?"
    }
  }
}
```

### Dibuat Dengan
[<img src='kotlin.svg' alt='kotlin' width='100' />](https://kotlinlang.org/)
[<img src='firebase.svg' alt='firebase' width='100' />](https://firebase.google.com/)
[<img src='arcore.svg' alt='ARCore' width='100' />](https://developers.google.com/ar)

## License
Distributed under the MIT License. See [LICENSE](LICENSE) for more information.
