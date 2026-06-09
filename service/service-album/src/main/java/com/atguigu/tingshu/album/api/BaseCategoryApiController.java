package com.atguigu.tingshu.album.api;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.common.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@Tag(name = "分类管理")
@RestController
@RequestMapping(value = "/api/album")
@SuppressWarnings({"all"})
public class BaseCategoryApiController {

    @Autowired
    private BaseCategoryService baseCategoryService;


    /**
     * 查询所有1级分类（包含2级分类以及3级分类列表）
     * @return [{categoryId:1,categoryName:"音乐",categoryChild:[{categoryId:101,categoryName:"音乐音效",categoryChild:[{categoryId:1001,categoryName:"催眠音乐"}]},{}]},{其他1级分类对象},{}]
     */
    @GetMapping("/category/getBaseCategoryList")
    public Result<List<JSONObject>> getBaseCategoryList(){
        List<JSONObject> list = baseCategoryService.getBaseCategoryList();
        return Result.ok(list);
    }


//    public static void main(String[] args) {
//        JSONObject c1_1 = new JSONObject();
//        c1_1.put("categoryId", 1001);
//        c1_1.put("categoryName", "催眠音乐");
//
//        JSONObject c1_2 = new JSONObject();
//        c1_2.put("categoryId", 1002);
//        c1_2.put("categoryName", "课程引导");
//        // 通过fastjson提供json对象(数组,对象)
//        JSONObject c1 = new JSONObject();
//        c1.put("categoryId", 1);
//        c1.put("categoryName", "音乐");
//        c1.put("categoryChild", Arrays.asList(c1_1,c1_2));
//
//        System.out.println(c1);
//    }

}

