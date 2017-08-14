# QR_Decode2

程式碼裡都有註解,建議都先不要動，先了解程式碼跟熟悉環境<br>
可以多多利用Log跟Toast了解參數變化<br><br>
流程:<br>
未掃描前flag為"clear"，掃描後為"waiting"，結帳成功後"complete" 然後回到"clear"<br><br>

以下為未完成:<br>
1.目前解碼判斷jsonobject=Null的地方還有點問題，如果資料是空的json的地方會crush掉<br>
2.waiting時要上傳客戶的商品條碼跟金錢<br>
3.complete後並回到clear時，要清除shopping cart裡的資料 <br>
