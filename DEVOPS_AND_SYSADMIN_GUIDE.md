# 🛠 راهنمای جامع DevOps، لینوکس و مدیریت سرور و پایگاه داده

> [!NOTE]
> این سند نسخه بازنویسی‌شده، بهینه‌سازی‌شده و توسعه‌یافته یادداشت‌های فنی و دستورالعمل‌های عملیاتی مدیریت سرور، جاوا، مایون، شبکه، پایگاه داده PostgreSQL و ابزارهای انتقال فایل در محیط‌های لینوکسی است.

---

## 📑 فهرست مطالب

1. [نصب و پیکربندی چندنسخه‌ای جاوا (JDK) در لینوکس](#۱-نصب-و-پیکربندی-چندنسخهای-جاوا-jdk-در-لینوکس)
2. [تنظیم متغیرهای محیطی سیستم (Environment Variables)](#۲-تنظیم-متغیرهای-محیطی-سیستم-environment-variables)
3. [نصب و مدیریت ابزار Build مایون (Apache Maven)](#۳-نصب-و-مدیریت-ابزار-build-مایون-apache-maven)
4. [انتقال امن فایل‌ها در بستر شبکه (SCP & Rsync)](#۴-انتقال-امن-فایلها-در-بستر-شبکه-scp--rsync)
5. [تونلینگ معکوس با SSH (Reverse SSH Tunneling)](#۵-تونلینگ-معکوس-با-ssh-reverse-ssh-tunneling)
6. [راه‌اندازی و مدیریت PostgreSQL با Docker](#۶-راهاندازی-و-مدیریت-postgresql-با-docker)
7. [راه‌اندازی Redis با Docker جهت کش و بلک‌لیست توکن‌ها](#۷-راهاندازی-redis-با-docker-جهت-کش-و-بلکلیست-توکنها)
8. [چک‌لیست نگهداری و عیب‌یابی در سرورهای لینوکس](#۸-چکلیست-نگهداری-و-عیبیابی-در-سرورهای-لینوکس)

---

## ۱. نصب و پیکربندی چندنسخه‌ای جاوا (JDK) در لینوکس

در صورتی که چندین نسخه جاوا (مانند JDK 17 و JDK 21) بر روی سرور نصب شده باشد، می‌توان از ابزار قدرتمند `update-alternatives` جهت مدیریت پویای نسخه پیش‌فرض بدون حذف سایر نسخه‌ها استفاده نمود.

### گام‌های پیکربندی دستی JDK در سیستم‌عامل‌های مبتنی بر دبیان/اوبونتو:

```bash
# بارگذاری متغیرهای محیطی پروفایل فعلی
source /etc/profile

# ثبت مسیر باینری java در سیستم آلترناتیو با اولویت ۱
sudo update-alternatives --install "/usr/bin/java" "java" "/opt/java/jdk-17.0.10/bin/java" 1
sudo update-alternatives --install "/usr/bin/java" "java" "/opt/java/21/bin/java" 2

# ثبت مسیر باینری کامپایلر javac
sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/java/jdk-17.0.10/bin/javac" 1
sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/java/21/bin/javac" 2

# تنظیم نسخه فعال به صورت خودکار یا دستی
sudo update-alternatives --set java /opt/java/21/bin/java
sudo update-alternatives --set javac /opt/java/21/bin/javac

# در صورت نیاز به منوی تعاملی برای انتخاب نسخه فعال:
sudo update-alternatives --config java
sudo update-alternatives --config javac

# راستی‌آزمایی نسخه فعال
java --version
javac --version
```

---

## ۲. تنظیم متغیرهای محیطی سیستم (Environment Variables)

به جای تعریف مستقیم متغیرها در `~/.bashrc` که مختص یک کاربر است، تعریف متغیرها در مسیر `/etc/profile.d/` باعث می‌شود تمام کاربران و سرویس‌های سیستمی به متغیرها دسترسی داشته باشند.

### ساخت اسکریپت پیکربندی جاوا:

فایل `/etc/profile.d/java.sh` را ایجاد کرده و محتوای زیر را در آن قرار دهید:

```bash
sudo tee /etc/profile.d/java.sh << 'EOF'
export JAVA_HOME=/opt/java/21
export PATH=$JAVA_HOME/bin:$PATH
EOF
```

> [!TIP]
> برای پروژه‌هایی که به JDK 17 نیاز دارند، مقدار `JAVA_HOME=/opt/java/jdk-17.0.10` قرار داده شود.

اعمال دسترسی اجرایی و بارگذاری فوری:

```bash
sudo chmod 755 /etc/profile.d/java.sh
source /etc/profile.d/java.sh
```

---

## ۳. نصب و مدیریت ابزار Build مایون (Apache Maven)

### ساخت اسکریپت پیکربندی مایون:

فایل `/etc/profile.d/maven.sh` را ایجاد نمایید:

```bash
sudo tee /etc/profile.d/maven.sh << 'EOF'
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
EOF
```

اعمال دسترسی و بارگذاری:

```bash
sudo chmod 755 /etc/profile.d/maven.sh
source /etc/profile.d/maven.sh

# بررسی صحت نصب
mvn --version
```

---

## ۴. انتقال امن فایل‌ها در بستر شبکه (SCP & Rsync)

برای انتقال فایل‌های حجیم مانند نسخه‌های پشتیبان پایگاه داده (`backup.sql`) یا پکیج‌های بیلد به سرورهای راه دور از پروتکل امن SSH استفاده می‌شود.

### دستور SCP (Secure Copy):

```bash
# ارسال فایل backup.sql به دایرکتوری Downloads کاربر mmad در سرور راه دور
scp backup.sql mmad@10.10.10.1:/home/admin/Downloads

# انتقال با مشخص کردن پورت سفارشی SSH (مثلاً پورت 2222)
scp -P 2222 backup.sql mmad@10.10.10.1:/home/admin/Downloads

# انتقال دایرکتوری به صورت بازگشتی (Recursive) همراه با فشرده‌سازی در حین انتقال (-C)
scp -r -C ./build/libs mmad@10.10.10.1:/opt/vertex/deployments/
```

### دستور Rsync (پیشنهادی برای فایل‌های حجیم و قابلیت ادامه‌یافتن):

```bash
# قابلیت Resume در صورت قطعی شبکه همراه با نمایش Progress Bar
rsync -avz --partial --progress backup.sql mmad@10.10.10.1:/home/admin/Downloads/
```

---

## ۵. تونلینگ معکوس با SSH (Reverse SSH Tunneling)

تونلینگ معکوس به شما اجازه می‌دهد تا سرویسی که در لوکال هاست شما فعال است (مثلاً سرویسی که روی پورت 20 یا 8080 سیستم محلی اجرا می‌شود) را از طریق یک سرور ریموت در دسترس قرار دهید، بدون اینکه نیاز به IP استاتیک یا پورت فورواردینگ مودم داشته باشید.

### فرمان برقراری تونل معکوس:

```bash
# فوروارد کردن پورت 8080 سرور ریموت به پورت 20 سیستم لوکال:
ssh -R 8080:localhost:20 mmad@10.10.10.1
```

### گزینه‌های پیشرفته جهت پایداری تونل:

```bash
# اجرای پس‌زمینه بدون اختصاص شل (-N) و ارسال سیگنال Keep-Alive
ssh -N -f -R 8080:localhost:8080 \
  -o ServerAliveInterval=60 \
  -o ServerAliveCountMax=3 \
  -o ExitOnForwardFailure=yes \
  mmad@10.10.10.1
```

> [!IMPORTANT]
> برای اینکه پورت باز شده روی سرور راه دور برای سایر سیستم‌ها در شبکه نیز در دسترس باشد، گزینه `GatewayPorts yes` باید در فایل `/etc/ssh/sshd_config` سرور ریموت فعال شده باشد.

---

## ۶. راه‌اندازی و مدیریت PostgreSQL با Docker

### ایجاد شبکه اختصاصی و Persistent Volume

استفاده از Volume برای پایگاه داده مانع از دست رفتن داده‌ها در صورت حذف یا بازنشانی کانتینر می‌شود:

```bash
# ایجاد Volume اختصاصی ذخیره‌سازی اطلاعات دیتابیس
sudo docker volume create pg-data

# ایجاد شبکه ایزوله بین دیتابیس و میکروسرویس‌ها
sudo docker network create pg-net
```

### اجرای کانتینر پایگاه داده PostgreSQL

```bash
sudo docker run --name pg \
  --volume pg-data:/var/lib/postgresql/data \
  --network pg-net \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=vertex_db \
  -p 5432:5432 \
  --restart unless-stopped \
  -d postgres:16-alpine
```

### عملیات روزمره پایگاه داده

```bash
# ورود به محیط خط فرمان تعاملی PostgreSQL (psql CLI)
sudo docker exec -it pg psql -U postgres

# ایجاد پایگاه داده جدید درون کانتینر
sudo docker exec -i pg psql -U postgres -c "CREATE DATABASE version;"

# بازیابی (Restore) فایل پشتیبان bu.sql به دیتابیس version
cat bu.sql | sudo docker exec -i pg psql -U postgres -d version

# تهیه پشتیبان (Backup / Dump) از پایگاه داده
sudo docker exec -t pg pg_dump -U postgres -d version > backup_$(date +%Y%m%d).sql
```

---

## ۷. راه‌اندازی Redis با Docker جهت کش و بلک‌لیست توکن‌ها

در پروژه Vertex، سرویس Redis برای ذخیره‌سازی کدهای کپچا، سشن‌ها، و Blacklist توکن‌های باطل‌شده JWT و Refresh Token استفاده می‌شود.

```bash
# ساخت volume برای داده‌های ردیس
sudo docker volume create redis-data

# راه‌اندازی کانتینر Redis
sudo docker run --name vertex-redis \
  --network pg-net \
  -p 6379:6379 \
  --volume redis-data:/data \
  --restart unless-stopped \
  -d redis:7-alpine redis-server --appendonly yes

# تست اتصال به ردیس و مانیتورینگ بلادرنگ کلیدها
sudo docker exec -it vertex-redis redis-cli ping
sudo docker exec -it vertex-redis redis-cli monitor
```

---

## ۸. چک‌لیست نگهداری و عیب‌یابی در سرورهای لینوکس

### بررسی وضعیت پورت‌ها و سوکت‌ها:
```bash
# مشاهده پروسه‌های گوش‌به‌زنگ روی پورت‌های سیستم
sudo ss -tulpn | grep -E '5432|6379|7801|8080|8888|7901'
```

### مانیتورینگ منابع مصرفی کانتینرها:
```bash
sudo docker stats --no-stream
```

### مشاهده لاگ‌های جاری با ساختار خوانا:
```bash
sudo docker logs --tail 100 -f pg
```
