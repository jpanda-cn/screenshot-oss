# 熊猫截图工具
> 一个简单的截图工具，核心特点是在完成截图工作之后，可以将图片自动上传到网络中(Git/OSS等)，并将图片对应的地址以指定的格式回填到剪切板中。


## 构建方式
项目比较简单，可以选择使用IDE工具启动，也可以选择jar包方式启动。
### jar包启动方式
1. 在项目根目录(pom.xml文件所处目录)执行命令`mvn jfx:jar`将会在`.\target\jfx\app`中生成一个`lib`目录以及一个名为`screenshot-oss-*-jfx.jar`的文件。

2. 使用命令`java -jar ./jpanda/screenshot-oss-1.0-*-jfx.jar`即可启动该工具。
### 打包成可执行文件
1. 在项目根目录(pom.xml文件所处目录)执行命令`mvn jfx:jar`将会在`.\target\jfx\app`中生成一个`lib`目录以及一个名为`screenshot-oss-*-jfx.jar`的文件。

2. 在项目根目录(pom.xml文件所处目录)执行命令`mvn jfx:native` 将会在`.\target\jfx\native`中生成一个可执行文件。
## 使用方式
启用该截图工具

- 首先是截图工具的主要窗口：
> 在该窗口主要有三个功能区，包括，选型下列框，截图按钮，截图时隐藏单选按钮。

![主窗口](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/faea0d06-c0d5-43d5-a4ac-1a8ecdf63c49.png)

其中选型下拉框中分为三个子选单：

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/014b0313-70f1-476b-a905-3d96038bce77.png)


- 通用设置
> 通用设置提供了管理截图工具基本功能的能力
其中主要包括，配置图片存储方式，剪切板内容，以及全局系统快捷键

![设置](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/3cedd34a-34a8-418f-8eb9-fbe6be8c4aed.png)

系统默认快捷键为`Ctrl + Shift + Alt + J`,快捷键可以在`选型`-`通用设置`-`系统快捷键`处设置，暂时未提供禁用快捷键的入口。
- 配置截图工具


  - 选择不同的存储方式

![存储方式](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/22954951-cda8-4892-b91f-0c303a6eb500.png)

  - 选择截图后在剪切板中放置的内容

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/f44a259f-3e0c-481d-bcd4-0506f27c5fc9.png)

  - 配置系统快键键

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/3545acdc-8842-4947-a3bb-06f80e913a33.png)

  - 配置当前存储方式需要使用的参数

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/4ba92f42-85e2-4540-bfb6-c5ff1900d580.png)

  - 比如git方式存储需要进行下列配置

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/7b6d4e11-d5ec-49d0-a3a9-ffd134ff0393.png)



- 密码管理
> 密码管理有两种状态，一种是当前已配置了密码，一种是当前未配置密码。

针对于已配置密码的情况，提供了停用密码和修改密码的能力，针对于未配置密码的情况，则提供新增密码的能力，但无论哪种情况，都不需要输入当前密码。

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/26691676-5bb8-48bc-b031-529c42827fab.png)

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/f55e41e9-3827-4f9b-806a-aaf4e22579ee.png)
- 失败任务列表
> 
![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/0b280974-39b9-47f8-a823-3ffc5b5026a3.png)

  - 查看异常详细信息
  
  ![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/7ffe4b80-3e04-48b6-8082-9c0ff3c03a0c.png)
  

- 展示截图窗口

![请输入图片描述](https://github.com/jpanda-cn/screenshot-oss/raw/master/docs/images/959d7c89-35e9-4bb8-8862-f15ef1a95db1.png)

## 主要工作流程
在执行截图工作的时候，主要流程是 
- 1.获取指定屏幕当前快照，提供给用户操作。
- 2.用户完成截图，程序将用户的截图区域生成图片。
- 3.程序执行存储操作，并返回图片存储的地址。
- 4.程序将图片存储地址交由剪切板回调器使用，剪切板回调器拿到用户截图和图片存储地址，决定执行何种操作，并将结果通过剪切板的形式体现给用户。

## 其他
程序简单的实现了一个容器的功能，并针对于被`Component`注解的类提供了构造注入`ViewContext`和`Configuration`对象的能力。

程序的启动由三部分构造:
- 1.引导启动，在引导启动的过程中，主要完成了程序需要依赖的一些基础操作。
- 2.引动启动完成后置操作，该操作将在引导启动完成后，程序正式提供服务前完成。
- 3.界面上下文启动，提供服务
## 名词解释
- 存储方式 
> 存储方式表示截图完成之后，生成的图片保存的位置。
- 剪切板内容
> 用户完成截图后，存放到剪切板中的数据。
- 截图鼠标跟随
> 截图鼠标跟随功能和选择屏幕是两个互斥的功能，截图鼠标跟随功能在截图时将会获取用户鼠标所处的屏幕快照，并将快照展示在鼠标所处的屏幕上。
- 选择屏幕
> 指定用户点击截图时获取的屏幕快照
- 截图预览
> 用户完成截图后，将弹出一个窗口，展示用户的截图内容。
## 扩展功能
通过注解的方式提供了扩展图片存储方式和剪切板回调的能力，详细参见注解`ImgStore`和`ClipType`.

## 注意事项
### 图片存储和剪切板回调的限制
> 截图操作主要涉及到三步操作:截图，处理截图，处理剪切板的内容。
截图操作是独立的，但是处理截图和处理剪切板是互相关联的。
对于处理截图的操作，有两种，一种是有图片存储地址的，一种是无图片存储的地址的。
对于剪切板操作，主要有两种类型，一种是存放图片，一种是存放地址，理论上暂不支持其他的类型。
针对无图片存储地址的截图方式，处理地址的剪切板操作显然是不被允许的，因此，这里在实现上也做了限制。
### 需要持久化保存的数据对象的限制
> 针对于用户的配置，专门定义了用户数据持久化的`Persistence`顶级接口，用户的实体类只要实现了该接口，就可以通过核心类`Configuration`来完成对其自动读取和存储的功能。
这里需要注意的是，针对数据存储对象专门提供了一个注解`Profile`,该注解一方面用于指定用户数据持久化对象保存的配置文件名称和对象的类型，另一方面可以确保其会被`PersistenceBeanRegistry`扫描到，避免发生期望之外的问题，比如：用户修改密码时，不会同步变更未添加该注解的数据配置。

## 版本规划
### v0.0.4(完成)
- 预期拆分现有截图保存按钮的功能，拆分为两个按钮，现有的`对号`形式的按钮，默认触发`不保存-图片`操作，而拆分出来的新的保存按钮，才会触发现有的配置，这样做的目的是为了避免使用该工具，误传图片。

- 为现有的截图保存操作，提供失败提醒和补偿机制，便于用户手动操作。

- 优化画笔和马赛克的撤销操作，将一次画笔和马赛克操作中的拖拽合而唯一。

- 优化:对于截图存储方式的配置的保存操作提供预校验的能力，即用于在对存储方式配置完成后，提供测试配置是否可用的能力。
