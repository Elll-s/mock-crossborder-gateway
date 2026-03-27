# 1. 寻找基础底座：工业级超轻量化 Alpine Linux + Java 17 JRE
FROM eclipse-temurin:17-jre-alpine

# 2. 设定集装箱内部的工作目录
WORKDIR /app

# 3. 把你之前用 Maven 编译好的胖包，生生塞进集装箱里，并改名叫 app.jar
COPY target/mock-crossborder-gateway-1.0-SNAPSHOT.jar app.jar

# 4. 在集装箱上开一个 8080 的物理孔洞
EXPOSE 8080

# 5. 当集装箱被吊车拉起时，自动执行的终极点火指令
ENTRYPOINT ["java", "-jar", "app.jar"]
