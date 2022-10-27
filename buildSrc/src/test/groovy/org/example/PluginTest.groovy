//package org.example
//
//import org.gradle.testkit.runner.GradleRunner
//import spock.lang.Specification
//import spock.lang.TempDir
//
//abstract class PluginTest extends Specification {
//    @TempDir
//    File testProjectDir
//    File settingsFile
//    File buildFile
//
////    def setup() {
//        settingsFile = new File(testProjectDir, 'settings.gradle').tap { it << "rootProject.name = 'my-org-conventions'" }
//        System.out.println("settings file: " + settingsFile);
//        buildFile = new File(testProjectDir, 'build.gradle')
//        System.out.println("buildFile file: " + buildFile);
//    }
//
//    def runTask(String task) {
//        System.out.println("testProjectDir: " + testProjectDir + " task: " + task);
//        return GradleRunner.create()
//                .withProjectDir(testProjectDir)
//                .withArguments(task, '--stacktrace')
//                .withPluginClasspath()
//                .build()
//    }
//
//    def runTaskWithFailure(String task) {
//        System.out.println("testProjectDir: " + testProjectDir + " task: " + task);
//        return GradleRunner.create()
//                .withProjectDir(testProjectDir)
//                .withArguments(task, '--stacktrace')
//                .withPluginClasspath()
//                .buildAndFail()
//    }
//}
