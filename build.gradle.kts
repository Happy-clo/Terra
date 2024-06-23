// 预发布设置
preRelease(true)

// 设置指定项目的版本
versionProjects(":common:api", version("6.4.3"))
versionProjects(":common:implementation", version("6.4.3"))
versionProjects(":platforms", version("6.4.3"))

// 对所有项目进行配置
allprojects {
    // 设置项目组
    group = "com.dfsek.terra"

    // 配置编译、依赖和发布相关设置
    configureCompilation()
    configureDependencies()
    configurePublishing()

    // 配置Java编译任务
    tasks.withType<JavaCompile>().configureEach {
        // 启用fork和增量编译
        options.isFork = true
        options.isIncremental = true
    }

    // 配置测试任务
    tasks.withType<Test>().configureEach {
        // 使用JUnit平台
        useJUnitPlatform()

        // 设置测试运行参数
        maxHeapSize = "2G"
        ignoreFailures = false
        failFast = true
        maxParallelForks = (Runtime.getRuntime().availableProcessors() - 1).takeIf { it > 0 } ?: 1

        // 配置测试报告
        reports.html.required.set(false)
        reports.junitXml.required.set(false)
    }

    // 对复制任务进行配置，排除重复文件
    tasks.withType<Copy>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    // 对JAR任务进行配置，排除重复文件
    tasks.withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

// 对特定项目进行后期评估配置
afterEvaluate {
    // 配置平台项目的分布
    forImmediateSubProjects(":platforms") {
        configureDistribution()
    }
    project(":platforms:bukkit:common").configureDistribution()

    // 对通用插件项目进行配置
    forSubProjects(":common:addons") {
        // 应用Shadow插件
        apply(plugin = "com.github.johnrengelman.shadow")

        // 配置构建任务，使其依赖于shadowJar任务
        tasks.named("build") {
            finalizedBy(tasks.named("shadowJar"))
        }

        // 配置Shadow JAR任务
        tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        }.configure {
            doLast {
                // 输出Shadow JAR的路径
                println("Shadow JAR路径: ${archiveFile.get().asFile.absolutePath}")
            }
        }
        
        // 配置依赖
        dependencies {
            "compileOnly"(project(":common:api"))
            "testImplementation"(project(":common:api"))
        }
    }
}