# 使用官方 Java 运行时镜像作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置维护者信息（可选）
#LABEL maintainer="your.email@example.com"

# 设置工作目录
WORKDIR /app

# 将构建好的 Spring Boot jar 包复制到容器中
COPY ./target/pic-hub-0.0.1-SNAPSHOT.jar /app/app.jar

# 设置环境变量以激活配置文件
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露应用程序运行所需的端口
EXPOSE 8123

# 定义启动命令
ENTRYPOINT ["java", "-jar", "/app/app.jar"]