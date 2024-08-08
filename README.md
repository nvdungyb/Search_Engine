* Ý tưởng: xây dựng công cụ gợi ý tìm kiếm đơn giản nhưng vẫn phải đáp ứng được yêu cầu.
*  Yêu cầu:
    + Gợi ý tìm kiếm phải phản hồi nhanh.
    + Kết quả gợi ý phải liên quan tới từ khóa tìm kiếm.

* Inputs:
    + ~3000 từ
    + ~75000 câu

* Version 1: Sử dụng Trie
  - Test hiệu suất trong khi lưu dữ liệu vào MongoDB.
    
      + Stream
        
        ![image](https://github.com/user-attachments/assets/54694918-fbe8-44c9-a356-60aa8ebf8fc9)
        
      + ParallelStream
            ![image](https://github.com/user-attachments/assets/1f1e3d2b-b9f7-4007-bfce-c60825c99062)




  - Lấy từ kết quả gợi ý từ database:
      +  ![image](https://github.com/user-attachments/assets/490c71a4-1360-4fa7-803c-5eadaac23310)

  - Lấy kết quả gợi ý từ Trie
      + Dùng đệ quy:
        
          ![image](https://github.com/user-attachments/assets/20cddf28-96ba-4f8f-a6e0-ad1174de7669)

      + Tối ưu:
          ![image](https://github.com/user-attachments/assets/f0fcf064-2ae5-4fe0-8d74-54e33df38e8a)




* Version 2: Sử dụng redis + MongoDB
  + cache hit and miss
  + Giới hạn k gợi ý, thuật toán giới hạn k
  + Thuật toán hạn chế truy nhập vào db 
