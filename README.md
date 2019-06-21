## 熊猫截图工具
> 一个简单的截图工具，在完成截图工作之后，可以将图片自动上传到网络中，并将图片对应的地址回填到剪切板中。

## 使用方式
项目比较简单，可以选择使用IDE工具启动，也可以选择jar包方式启动。
在项目根目录(pom.xml文件所处目录)执行命令`mvn jfx:build-jar`将会在`.\target\jfx\app`中生成一个`lib`目录以及一个名为`screenshot-oss-*-jfx.jar`的文件。
使用命令`java -jar ./jpanda/screenshot-oss-1.0-*-jfx.jar`即可启动该工具。

系统默认快捷键为`Ctrl + Shift + Alt + J`,快捷键可以在`选型`-`通用设置`-`系统快捷键`处设置，暂时未提供禁用快捷键的入口。
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

