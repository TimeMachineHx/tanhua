package com.tanhua.server.controller;

import com.tanhua.server.dto.RecommendUserQueryParam;
import com.tanhua.server.service.TodayBestService;
import com.tanhua.common.utils.Cache;
import com.tanhua.server.vo.PageResult;
import com.tanhua.server.vo.TodayBest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: my-tanhua
 * @description:
 * @author: HongXin
 * @create: 2021-08-30 20:48
 */

@RestController
@RequestMapping("tanhua")
@Slf4j
public class TodayBestController {
    @Autowired
    private TodayBestService todayBestService;

    /**
     * 查询今日佳人
     *
     *
     * @return
     */
    @GetMapping("todayBest")
    public ResponseEntity<TodayBest> queryTodayBest() {
        try {
            TodayBest todayBest = todayBestService.queryTodayBest();

            if (todayBest != null) {
                return ResponseEntity.status(HttpStatus.OK).body(todayBest);
            }
        } catch (Exception e) {
            log.error("查询失败", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }


    /**
     * 查询推荐用户列表
     *
     *
     * @param queryParam
     * @return
     */
    @GetMapping("recommendation")
    @Cache(time = "60")
    public ResponseEntity<PageResult> queryRecommendation(RecommendUserQueryParam queryParam) {
        try {
            PageResult pageResult = todayBestService.queryRecommendation(queryParam);
            if(pageResult != null){
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            log.error("查询列表出错");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
