#Android组件化开发插件
## 功能： 
    帮助开发者快速创建组件化项目，快速对组件化模块进行配置，插件分两个功能，创建项目和创建模块

### 1.创建项目

*  1.自动创建对应包名的项目。
*  2.自动创建config.gradle配置文件和引入
*  3.自动划分业务包名
*  4.自动加入公司信息

### 2.创建模块
*  1.自动创建模块
*  2.自动配置build.gradle文件
*  3.自动添加settings.gradle文件
*  4.自动创建libmanifest
*  当创建模块之后，只需要在config.gradle中的moduleSetting中加入  【module名】RunAlone 布尔变量进行控制即可。 