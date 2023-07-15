package com.yuqian.itax.yishui.service;


import com.yuqian.itax.util.util.yishui.AESUtils;
import com.yuqian.itax.yishui.entity.YsMerConfig;

public class YiShuiNotifyService {

    public static void main(String[] args) {
        YsMerConfig config = new YsMerConfig();
        //create_batch_order
        String resoult = "YN2ZCwrCNyryQxqy442rXOZsljk0uSvnpYplJZh2wVqBv6ZfXva09/5RRcwdSKTqjl57usGeOJmDJgAuoe6vhR8Vi3jiNgwRrmh2C14Y4TcMa9h3BSHp2GDbBoPIopven3639ZPR/2QbM5KpY03U7cme3KPF9+kP/pXT+j9UHsrP9gPpnsbjYc8pTQ9/Y3L2COi5FIzCDwW8jyAF1oju4g==";

        resoult = "YN2ZCwrCNyryQxqy442rXOZsljk0uSvnpYplJZh2wVpH8rOdKkeQopciWnXX394Jjl57usGeOJmDJgAuoe6vhR8Vi3jiNgwRrmh2C14Y4TcMa9h3BSHp2GDbBoPIopven3639ZPR/2QbM5KpY03U7cme3KPF9+kP/pXT+j9UHsrh93hVmGq9Q5mzMVBm0TcsCOi5FIzCDwW8jyAF1oju4g==";

        resoult = "YN2ZCwrCNyryQxqy442rXOZsljk0uSvnpYplJZh2wVqoSZRQFn6eI1/QCiewzjHDjl57usGeOJmDJgAuoe6vhR8Vi3jiNgwRrmh2C14Y4TcMa9h3BSHp2GDbBoPIopven3639ZPR/2QbM5KpY03U7cme3KPF9+kP/pXT+j9UHsogeQQl9DiVbz2rccYBV8ijCOi5FIzCDwW8jyAF1oju4g==";
        //解密
        String data = AESUtils.decrypt(resoult,config.getAseKey());
        //{"trade_number":"YC20230208094700006","data":[{"result_code":"SUCCESS","enterprise_order_id":65102,"states":"M"}]}
        System.out.println(data);

    }
}
