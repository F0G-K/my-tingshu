package com.atguigu.tingshu.album.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.tingshu.album.mapper.BaseCategory1Mapper;
import com.atguigu.tingshu.album.mapper.BaseCategoryViewMapper;
import com.atguigu.tingshu.album.service.BaseCategoryService;
import com.atguigu.tingshu.model.album.BaseCategory1;
import com.atguigu.tingshu.model.album.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategoryService {

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    /**
     * 查询所有分类：一级分类包含二级分类，二级分类包含三级分类
     *
     * @return 业务数据：
     * [
     * {
     * "categoryId": 1,
     * "categoryName": "音乐",
     * "categoryChild": [
     * {
     * "categoryId": 101,
     * "categoryName": "音乐音效",
     * "categoryChild": [
     * {
     * "categoryId": 1001,
     * "categoryName": "催眠音乐"
     * }
     * ]
     * }
     * ]
     * }
     * ]
     */
//	@Override
//	public List<JSONObject> getBaseCategoryList() {
//		// 1. 查询分类视图数据
//		List<BaseCategoryView> allCategoryList = baseCategoryViewMapper.selectList(null);
//
//		// 2. 创建响应结果集合，用于封装所有一级分类
//		List<JSONObject> returnList = new ArrayList<>();
//
//		// 3. 按一级分类 ID 分组
//		// 使用 LinkedHashMap 保证分组后的遍历顺序与查询结果顺序一致
//		Map<Long, List<BaseCategoryView>> category1Map = allCategoryList.stream()
//				.collect(Collectors.groupingBy(
//						BaseCategoryView::getCategory1Id,
//						LinkedHashMap::new,
//						Collectors.toList()
//				));
//
//		// 4. 遍历一级分类
//		for (Map.Entry<Long, List<BaseCategoryView>> entry1 : category1Map.entrySet()) {
//			List<BaseCategoryView> category1List = entry1.getValue();
//			BaseCategoryView category1View = category1List.get(0);
//
//			JSONObject jsonObject1 = new JSONObject();
//			jsonObject1.put("categoryId", category1View.getCategory1Id());
//			jsonObject1.put("categoryName", category1View.getCategory1Name());
//
//			// 5. 在当前一级分类下，按二级分类 ID 分组
//			Map<Long, List<BaseCategoryView>> category2Map = category1List.stream()
//					.collect(Collectors.groupingBy(
//							BaseCategoryView::getCategory2Id,
//							LinkedHashMap::new,
//							Collectors.toList()
//					));
//
//			List<JSONObject> jsonObject2List = new ArrayList<>();
//
//			// 6. 遍历二级分类
//			for (Map.Entry<Long, List<BaseCategoryView>> entry2 : category2Map.entrySet()) {
//				List<BaseCategoryView> category2List = entry2.getValue();
//				BaseCategoryView category2View = category2List.get(0);
//
//				JSONObject jsonObject2 = new JSONObject();
//				jsonObject2.put("categoryId", category2View.getCategory2Id());
//				jsonObject2.put("categoryName", category2View.getCategory2Name());
//
//				// 7. 封装三级分类
//				List<JSONObject> jsonObject3List = new ArrayList<>();
//
//				for (BaseCategoryView baseCategoryView : category2List) {
//					JSONObject jsonObject3 = new JSONObject();
//					jsonObject3.put("categoryId", baseCategoryView.getCategory3Id());
//					jsonObject3.put("categoryName", baseCategoryView.getCategory3Name());
//
//					jsonObject3List.add(jsonObject3);
//				}
//
//				jsonObject2.put("categoryChild", jsonObject3List);
//				jsonObject2List.add(jsonObject2);
//			}
//
//			jsonObject1.put("categoryChild", jsonObject2List);
//			returnList.add(jsonObject1);
//		}
//
//		return returnList;
//	}
    @Override
    public List<JSONObject> getBaseCategoryList() {
        //创建相应结果集合对象-用于封装所有一级分类对象
        List<JSONObject> returnList = new ArrayList<>();
//		查全部数据
        List<BaseCategoryView> allCategoryList = baseCategoryViewMapper.selectList(null);
//		按一级分类分组
        Map<Long, List<BaseCategoryView>> category1Map = allCategoryList.stream()
                .collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
//				遍历一级分类
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : category1Map.entrySet()) {
//		创建封装一级分类对象
            JSONObject jsonObject1 = new JSONObject();
            //封装一级分类的ID
            Long category1Id = entry1.getKey();
            jsonObject1.put("categoryId", category1Id);
            //封装一级分类的名称
            String category1Name = entry1.getValue().get(0).getCategory1Name();
            jsonObject1.put("categoryName", category1Name);
//				按二级分类分组
            List<JSONObject> jsonObject2List = new ArrayList<>();
            Map<Long, List<BaseCategoryView>> category2Map = entry1.getValue()
                    .stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
//		遍历二级分类
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : category2Map.entrySet()) {
                //封装二级分类对象
                JSONObject jsonObject2 = new JSONObject();
                //获取二级分类ID
                Long category2ID = entry2.getKey();
                //将二级分类ID放进对象里
                jsonObject2.put("categoryId", category2ID);
                //封装二级分类名称
                String category2Name = entry2.getValue().get(0).getCategory2Name();
                //同上
                jsonObject2.put("categoryName", category2Name);
                //将二级分类对象放到二级分类集合中
                jsonObject2List.add(jsonObject2);
                //处理三级分类
                //封装三级分类对象
                List<JSONObject> jsonObject3List = new ArrayList<>();
//		遍历三级分类
                for (BaseCategoryView baseCategoryView : entry2.getValue()) {
                    //封装三级分类json object对象
                    JSONObject jsonObject3 = new JSONObject();
                    //封装三级分类id
                    jsonObject3.put("categoryId", baseCategoryView.getId());
                    //封装三级分类名称
                    jsonObject3.put("categoryName", baseCategoryView.getCategory3Name());
                    //将三级分类对象放进集合中
                    jsonObject3List.add(jsonObject3);
                }
                //三级放进二级
                jsonObject2.put("categoryChild", jsonObject3List);
            }
//				二级放进一级
            jsonObject1.put("categoryChild", jsonObject2List);
//			一级放进返回集合
            returnList.add(jsonObject1);
        }
//				返回结果
        return returnList;
    }
}
