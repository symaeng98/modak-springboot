//package com.modak.modakapp.controller;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Api(value="ApiController v1")
//@RequestMapping("/")
//public class ApiController {
//
//    @ApiOperation(value="덧셈", notes="덧셈 사칙연산")
//    @GetMapping(value = "/add")
//    public ResponseEntity<Integer> add(
//            @ApiParam(value="첫째 값", required=true, example="1")
//            @RequestParam(value = "num1", required = true) int num1,
//            @ApiParam(value="두번째 값", required=true, example="2")
//            @RequestParam(value = "num2", required = true) int num2) {
//
//        int sum = num1 + num2;
//
//        return ResponseEntity.ok(sum);
//    }
//
//    @ApiOperation(value="뺄셈", notes="뺄셈 사칙연산")
//    @GetMapping(value = "/minue")
//    public ResponseEntity<Integer> minue(
//            @ApiParam(value="첫째 값", required=true, example="3")
//            @RequestParam(value = "num1", required = true) int num1,
//            @ApiParam(value="두번째 값", required=true, example="4")
//            @RequestParam(value = "num2", required = true) int num2) {
//
//        int minus = num1 + num2;
//
//        return ResponseEntity.ok(minus);
//    }
//
//    @ApiOperation(value="곱셈", notes="곱셈 사칙연산")
//    @GetMapping(value = "/multiply")
//    public ResponseEntity<Double> multiply(
//            @ApiParam(value="첫째 값", required=true, example="5")
//            @RequestParam(value = "num1", required = true) int num1,
//            @ApiParam(value="두번째 값", required=true, example="6")
//            @RequestParam(value = "num2", required = true) int num2) {
//
//        double res = num1 * num2;
//
//        return ResponseEntity.ok(res);
//    }
//
//    @ApiOperation(value="나눗셈", notes="나눗셈 사칙연산")
//    @GetMapping(value = "div")
//    public ResponseEntity<Double> div(
//            @ApiParam(value="첫째 값", required=true, example="3")
//            @RequestParam(value = "num1", required = true) int num1,
//            @ApiParam(value="두번째 값", required=true, example="2")
//            @RequestParam(value = "num2", required = true) int num2) {
//
//        double res = num1 / num2;
//
//        return ResponseEntity.ok(res);
//    }
//}