package com.vertex.oauth.controller;

import com.vertex.core.api.oauth.DocumentApi;
import com.vertex.core.dto.DocumentDto;
import com.vertex.core.util.PagedResponse;
import com.vertex.core.util.ResponseMessage;
import com.vertex.core.util.RestResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DocumentController implements DocumentApi {

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).MAIN_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public PagedResponse<DocumentDto.Response> getList(DocumentDto.Search search, Pageable pageable) {
        List<DocumentDto.Response> dummyList = new ArrayList<>();
        dummyList.add(new DocumentDto.Response(
                1L,
                "Microservice-Architecture-Guide.pdf",
                "pdf",
                1024L * 1024 * 3,
                "application/pdf",
                LocalDateTime.now().minusDays(2),
                "/api/documents/1/download"
        ));
        dummyList.add(new DocumentDto.Response(
                2L,
                "OAuth2-JWT-Security-Specification.docx",
                "docx",
                1024L * 512,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                LocalDateTime.now().minusDays(5),
                "/api/documents/2/download"
        ));
        dummyList.add(new DocumentDto.Response(
                3L,
                "Database-Schema-Entity-Mapping.png",
                "png",
                1024L * 850,
                "image/png",
                LocalDateTime.now().minusDays(8),
                "/api/documents/3/download"
        ));
        return PagedResponse.ok(dummyList, dummyList.size(), false);
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).MAIN_MENU, T(com.vertex.core.enums.PermissionEnum).READ)")
    @Override
    public RestResponse<List<DocumentDto.SystemDoc>> getSystemDocs() {
        List<DocumentDto.SystemDoc> docs = new ArrayList<>();

        docs.add(new DocumentDto.SystemDoc(
                "doc-architecture",
                "معماری میکروسرویس و توپولوژی سیستم",
                "معماری",
                "تحلیل و تشریح ساختار میکروسرویس‌های اکوسیستم Vertex شامل Eureka، Config Server، Gateway، OAuth Server و فرانت‌اند Vaadin.",
                "1.0.0",
                "تایید شده",
                """
                ### معماری کلان سیستم (Vertex Microservice Architecture)

                اکوسیستم **Vertex** با استفاده از الگوهای توزیع‌شده مدرن طراحی شده است:

                1. **Eureka Server (Port 7801)**: رجیستری مرکزی و کشف پویای سرویس‌ها (Service Discovery).
                2. **Config Server (Port 8888)**: مدیریت متمرکز فایل‌های کانفیگ به صورت Git-backed و رمزنگاری با Jasypt.
                3. **Gateway Server (Port 8080)**: درگاه ورودی واحد، مسیریابی و کنترل ترافیک به سمت میکروسرویس‌ها.
                4. **OAuth Server (Port 7901)**: سرور احراز هویت متمرکز (JWT + Refresh Token)، RBAC و نگهداری کاربران.
                5. **Stellar UI**: فریم‌ورک اشتراکی فرانت‌اند با طراحی یکپارچه Vaadin 25، تم Aura و تایپوگرافی وزیرمتن.
                6. **Stellar OAuth**: وب‌اپلیکیشن مدیریت دسترسی و هویت ساخته‌شده تماماً با Java و فریم‌ورک Vaadin.
                """
        ));

        docs.add(new DocumentDto.SystemDoc(
                "doc-security",
                "امنیت، مدیریت توکن‌ها و رفرش توکن",
                "امنیت",
                "فرآیند تولید، چرخش (Rotation) و ابطال توکن‌های JWT و Refresh Token، سازوکار کپچا و کنترل دسترسی بر مبنای نقش (RBAC).",
                "2.1.0",
                "تایید شده",
                """
                ### سازوکار امنیتی و احراز هویت

                - **Access Token**: دارای طول عمر محدود (۳۰ تا ۶۰ دقیقه) جهت دسترسی به اندپوینت‌های محافظت‌شده.
                - **Refresh Token**: دارای طول عمر بلندمدت (۱۴ روز) با قابلیت Rotation؛ هنگام دریافت توکن جدید، رفرش توکن قبلی ابطال می‌گردد.
                - **Blacklist متمرکز در Redis**: در زمان خروج (Logout) یا چرخش توکن، امضای توکن در ردیس ثبت شده و از حملات Replay جلوگیری می‌شود.
                - **رمزنگاری متقارن و نامتقارن**: انتقال داده‌های ورود رمزنگاری‌شده با RSA و هش کردن پسوردها با الگوریتم HMAC/BCrypt.
                """
        ));

        docs.add(new DocumentDto.SystemDoc(
                "doc-api-ref",
                "راهنمای APIهای مدیریت کاربران، نقش‌ها و مقادیر ثابت",
                "API",
                "کاتالوگ متدهای REST و کلاینت‌های OpenFeign به همراه نمونه فرمت‌های درخواست و پاسخ JSON.",
                "1.2.0",
                "عملیاتی",
                """
                ### اندپوینت‌های کلیدی سیستم

                - `POST /api/user/page`: دریافت لیست کاربران با صفحه‌بندی و فیلتر
                - `POST /api/user`: ایجاد کاربر جدید با تخصیص نقش
                - `PUT /api/user/{id}`: به‌روزرسانی مشخصات و وضعیت حساب کاربر
                - `DELETE /api/user/{id}`: حذف کاربر
                - `POST /api/role/list`: لیست نقش‌های سیستم
                - `POST /api/permission/sync`: همگام‌سازی ماتریس دسترسی نقش و منو
                - `POST /api/code-type/list`: لیست دسته‌بندی مقادیر ثابت
                - `POST /api/code-type-item/list`: لیست اقلام مقادیر ثابت
                """
        ));

        return RestResponse.ok(docs);
    }

    @PreAuthorize("hasPermission(T(com.vertex.oauth.config.OauthMenu).MAIN_MENU, T(com.vertex.core.enums.PermissionEnum).DELETE)")
    @Override
    public ResponseMessage delete(Long id) {
        return ResponseMessage.success();
    }
}
