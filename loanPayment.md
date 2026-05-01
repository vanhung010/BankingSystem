Luồng thanh toán nợ khoản vay

1. Sang tháng mới, hệ thống sẽ tự cộng tiền lãi vào balance của tất cả tài khoản vay, tạo giao dịch

2. Hệ thống sẽ hiện số tiền cần thanh toán tối thiểu trong tháng để không bị khóa tài khoản

3. Người dùng thực hiện thanh toán.

4. Nếu sang tháng mới mà số tiền tối thiểu phải trả ở tháng trước chưa = 0 thì sẽ thực hiện khóa tài khoản. (Chuyển trạng thái sang LOCKED có thể liên hệ bên ngân hàng để mở)

5. Nếu balance của tài khoản vay = 0 thì sẽ thực hiện đóng tài khoản

6. Nếu hết hạn(Ngày hệ thống > ngày hết hạn) của khoản vay mà chưa thanh toán hết thì sẽ thực hiện khóa tài khoản và hiện thông báo lên ngân hàng

