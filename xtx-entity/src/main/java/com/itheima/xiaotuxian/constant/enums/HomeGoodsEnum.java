package com.itheima.xiaotuxian.constant.enums;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion:
 */
public enum HomeGoodsEnum {
//DATA1("https://yjy-oss-files.oss-cn-zhangjiakou.aliyuncs.com/tuxian/fresh_goods_cover.jpg", "全场3件8折"),
    DATA1("https://yanxuan-item.nosdn.127.net/01227c93e9098342be591ea57b8953c7.jpg?quality=95&thumbnail=610x610&imageView", "全场3件8折"),
    DATA2("https://yjy-oss-files.oss-cn-zhangjiakou.aliyuncs.com/tuxian/home_goods_cover.jpg", "3折起狂欢"),
    DATA3("https://yjy-oss-files.oss-cn-zhangjiakou.aliyuncs.com/tuxian/kitchen_goods_cover.jpg", "大额优惠\r\n等你来拿"),
    DATA4("https://yanxuan-item.nosdn.127.net/0d7d091a10faf1c22027046f517511cf.png?quality=95&thumbnail=610x610&imageView", "全场3件8折"),
    DATA5("https://yanxuan-item.nosdn.127.net/30959f7fcf980de2be0a6e1937938d45.png?quality=95&thumbnail=610x610&imageView", "3折起狂欢"),
    DATA6("https://yanxuan-item.nosdn.127.net/d0176b163973961ed01818d04ee94a06.png?quality=95&thumbnail=610x610&imageView", "大额优惠\r\n等你来拿");

    private String picture;
    private String saleInfo;

    HomeGoodsEnum(String picture, String saleInfo) {
        this.picture = picture;
        this.saleInfo = saleInfo;
    }

    public String getPicture() {
        return picture;
    }

    public String getSaleInfo() {
        return saleInfo;
    }
}
