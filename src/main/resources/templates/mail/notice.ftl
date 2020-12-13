<div class="emailpaged" style="background: #fff;">
    <div class="emailcontent"
         style="width:100%;max-width:720px;text-align: left;margin: 0 auto;padding-top: 20px;padding-bottom: 80px">
        <div class="emailtitle" style="border-radius: 5px;border:1px solid #eee;overflow: hidden;">
            <h1
                    style="color:#fff;background: #3798e8;line-height:70px;font-size:24px;font-weight:normal;padding-left:40px;margin:0">
                ${title!}
            </h1>
            <div class="emailtext" style="background:#fff;padding:20px 32px 40px;">
                <p style="color: #6e6e6e;font-size:13px;line-height:24px;">${targetUserName!}, 您好!</p>
                <p style="color: #6e6e6e;font-size:18px;line-height:24px;">${content!}</p>
                <hr />
                <p style="color: #6e6e6e;font-size:13px;line-height:24px;">通知发起人：${publisherName!}</p>
                <p style="color: #6e6e6e;font-size:13px;line-height:24px;">您可以扫描以下二维码进入小程序：</p>
                <div style="text-align: center;">
                    <img width="200px" height="200px" src="${miniCodeUrl}" alt="">
                </div>
            </div>
        </div>
    </div>
</div>