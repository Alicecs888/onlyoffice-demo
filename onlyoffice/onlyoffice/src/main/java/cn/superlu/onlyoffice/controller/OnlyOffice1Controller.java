//package cn.superlu.onlyoffice.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//import static javax.swing.UIManager.put;
//
//@RestController
//@RequestMapping("/onlyoffice")
//@Slf4j
//public class OnlyOffice1Controller {
//
//    /**
//     * 1 - 正在编辑文档，
//     * 2 - 文档已准备好保存，
//     * 3 - 发生文档保存错误，
//     * 4 - 文档已关闭，没有任何更改，
//     * 6 - 正在编辑文档，但保存了当前文档状态，
//     * 7 - 强制保存文档时发生错误。
//     */
//    private static final Map<Integer, String> documentStatusMap;
//
//    static {
//        Map<Integer, String> aMap = new HashMap<>();
//        aMap.put(1, "status: 1 正在编辑文档");
//        aMap.put(2, "status: 2 文档已准备好保存");
//        aMap.put(3, "status: 3 发生文档保存错误");
//        aMap.put(4, "status: 4 文档已关闭，没有任何更改");
//        aMap.put(6, "status: 6 正在编辑文档，但保存了当前文档状态");
//        aMap.put(7, "status: 7 强制保存文档时发生错误");
//        documentStatusMap = Collections.unmodifiableMap(aMap);
//    }
//
//    /**
//     * @param request
//     * @param response
//     * @Author: shun CHENG
//     * @Date: 2025/4/11
//     * @return: java.lang.String
//     * @Description: 回调接口
//     * <p>
//     * 集成minio+onlyoffice实现在线编辑 可能出现的问题
//     * <p>
//     * 用户a先去获取文件，用户b也去获取文件，用户a先保存了文件，用户b也保存了文件，那么用户b的文件会覆盖用户a的文件。
//     * 但是对于onlyoffice而言如果多个用户打开的是同一个文件，那么用户a和用户b就是同时在线编辑的操作,问题不大。
//     * <p>
//     * 如果用户a去获取文件之后编辑，编辑结束后删除原文件去上传，这时候用户b去获取文件则会出现获取不到文件的情况，因为此时用户a的文件可能没有上传上去。
//     * 如果用户a是上传文件之后再去删除原文件，那么在上传文件的过程中会出现用户b去获取原文件然后去修改的情况，造成读未提交。
//     */
//    @RequestMapping("/callback")
//    public String callback(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
//        String body = scanner.hasNext() ? scanner.next() : "";
//
//        JSONObject jsonObj = JSONObject.parseObject(body);
//        log.info("{}", documentStatusMap.get(jsonObj.get("status")));
//        if ((int) jsonObj.get("status") == 2) {
//            String downloadUri = (String) jsonObj.get("url");
//            URL url = new URL(downloadUri);
//            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
//            InputStream stream = connection.getInputStream();
//            String fileName = cn.hutool.core.lang.UUID.randomUUID().toString().replace("-", "") + "_create." + jsonObj.get("filetype");
//            String templatePath = getClass().getClassLoader().getResource("").getPath();
//            templatePath += fileName;
//            log.info("文件保存地址：" + templatePath);
//            File tempFile = new File(templatePath);
//            try (FileOutputStream out = new FileOutputStream(tempFile)) {
//                int read;
//                final byte[] bytes = new byte[1024];
//                while ((read = stream.read(bytes)) != -1) {
//                    out.write(bytes, 0, read);
//                }
//                out.flush();
//            }
//            connection.disconnect();
//        }
//        return "{\"error\":0}";
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // 验证文件类型（仅允许Office格式）
//            String fileName = file.getOriginalFilename();
//            if (fileName == null || fileName.isEmpty()) {
//                return ResponseEntity.badRequest().body("无效的文件名");
//            }
//
//            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//            if (!Arrays.asList("docx", "xlsx", "pptx").contains(extension)) {
//                return ResponseEntity.badRequest().body("不支持的文件类型");
//            }
//
//            // 保存文件到本地
//            Path path = Paths.get("D:\\doc", fileName);
//            try (var inputStream = file.getInputStream()) {
//                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
//            }
//
//            // 返回文件URL（供ONLYOFFICE调取）
//            String fileUrl = "http://localhost:8089/files/" + fileName;
//            log.info("文件上传成功，文件URL：{}", fileUrl);
//            return ResponseEntity.ok(fileUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("上传失败");
//        }
//    }
//}
