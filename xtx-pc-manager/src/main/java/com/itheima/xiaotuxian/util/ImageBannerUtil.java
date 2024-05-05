package com.itheima.xiaotuxian.util;


import cn.hutool.core.util.RandomUtil;
import com.itheima.xiaotuxian.vo.classification.ImageBannerVo;

import java.util.ArrayList;
import java.util.List;
/*
 * @author: lbc
 * @Date: 2023-04-29 17:25:37
 * @Descripttion: 模拟广告图片的，目前已不再使用，已经全部从数据库加载了
 */
@Deprecated
public class ImageBannerUtil {
    private static List<ImageBannerVo> mockImageBanners = new ArrayList<>();

    static {
        mockImageBanners.add(new ImageBannerVo("轮播图1", "https://yanxuan-item.nosdn.127.net/d66e9010b01c33eee075ed66f0822c36.png?type=webp&imageView&thumbnail=430x430&quality=95", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图2", "https://yanxuan-item.nosdn.127.net/bf8a46715947c09ab8d09071c1ae650b.jpg?type=webp&imageView&thumbnail=430x430&quality=95", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图3", "https://yanxuan-item.nosdn.127.net/86a91475bca4cb758c3f67733c0916de.png?type=webp&imageView&thumbnail=430x430&quality=95", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图4", "https://yanxuan-item.nosdn.127.net/b19e9295575c3c45d946e2d544595d48.jpg?type=webp&imageView&thumbnail=430x430&quality=95", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图5", "https://yanxuan-item.nosdn.127.net/1fdbd160df259819513cb44ce87a9f11.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图6", "https://yanxuan-item.nosdn.127.net/7061fe20c42bde889c587db5c0fc28b5.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图7", "https://yanxuan-item.nosdn.127.net/3b2fd1177e3df47ec419e4032fa592b0.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图8", "https://yanxuan-item.nosdn.127.net/5354562ab2a37cf55376516eb4f3c9f2.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图9", "https://yanxuan-item.nosdn.127.net/add67f6d5c7a90e7463f1a58d8a82ef3.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图10", "https://yanxuan-item.nosdn.127.net/f692880316a0d5848f68e00486f4d560.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图11", "https://yanxuan-item.nosdn.127.net/8adbbfd1203640aa8e253fdec4407dbe.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
        mockImageBanners.add(new ImageBannerVo("轮播图12", "https://yanxuan-item.nosdn.127.net/ebf8bcf1265fd8244e5bf459579c0b99.jpg?type=webp&quality=95&thumbnail=245x245&imageView", "https://gs.ctrip.com/html5/you/travels/1422/3771516.html?from=https%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"));
    }

    /**
     * 随机获取banner集合
     *
     * @return banner集合
     */
    public static List<ImageBannerVo> getMockImageBanners() {
        var size = RandomUtil.randomInt(1, 6);
        var results = new ArrayList<ImageBannerVo>(size);
        var temps = new ArrayList<ImageBannerVo>(mockImageBanners);
        while (results.size() < size) {
            var index = RandomUtil.randomInt(temps.size() - 1);
            results.add(temps.get(index));
            temps.remove(index);
        }
        return results;
    }
}
