# 使用 Maven 镜像进行构建
FROM maven:3.9-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段：使用轻量级 JRE 镜像
FROM eclipse-temurin:17-jre-jammy

# 安装 wget 用于健康检查
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件（使用通配符以支持版本变化）
# 注意：如果target目录中有多个JAR文件，此命令会失败并显示错误
COPY --from=builder /app/target/*.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 设置启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

