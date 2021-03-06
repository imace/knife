Home                {#mainpage}
========

# 简介

Knife是一个Java应用的运行时命令行诊断工具。 有点像[Btrace](http://kenai.com/projects/btrace/). 提供了很多Btrace没有提供的功能.

# 功能

与Btrace类似的功能:

1. 支持在运行时被加载到jvm中，不需要目前应用做任何修改.
2. 可以跟踪一个方法调用，并打印出入参、返回结果和调用时间等信息.

Btrace所没有提供的功能:

1. 命令行的交互方式，不需要编写脚本.
2. 可以遍历堆内存，找到目标对象. 
3. 可以查看并修改某个对象的属性值.
4. 可以主动调用某个方法，并可以跟踪调用的返回结果和调用时间等信息.
5. 可以查找某个对象引用的所有对象或引用某个对象的所有对象.

# 下载

http://code.google.com/p/knife/downloads/list

# 如何使用Knife?
1. 下载 knife-XXX.tar.gz
2. 解压缩“tar -zxvf knife-XXX.tar.gz”
3. 执行knife.sh

## 本地attach和远程attach

![连接到本地运行的Jvm或远程的Jvm](attach.png)

连接到本地运行的Jvm或远程的Jvm

![连接成功](connected.jpg)

# 已实现的命令

## find

find命令用于寻找堆内存中的某个对象，主要思路为遍历堆内存，根据类名查找某个类的所有实例，然后再选择实际需要的某个实例。

~~~~~~~~~~
find <find-expretion>
~~~~~~~~~~

 <h3>find-expretion</h3> 

完整类名或查找结果中类名的序号.

> 查找名字中包含符合 'Service' 的类，如果匹配结果有多个类，会把这些类遍历出来，如果匹配结果只有一个类，会显示这个类的所有实例。

![查找名字中包含符合 'Service' 的类](find_service.jpg)

> 在堆内存中查找类 'com.chenjw.knife.server.test.impl.ApplyServiceImpl' 的实例.
> 也可以直接使用 'find 15' , '15' 表示上一次查找结果中类的序号.

![查找之前返回结果中序号为 '15' 也就是类 'com.chenjw.knife.server.test.impl.ApplyServiceImpl' 的所有实例](find_target_service.jpg)

## cd

设置某个对象为“目标对象”. 类似'invoke' 和 'ls' 这些命令都是针对目标“对象操作”的.

任何时候当输出中包含 '@15' 这样的标记时 (比如 'find' 命令的结果, 或 'invoke' 命令的返回值和参数), 这个对象都可以通过 'cd 15' 的方式标记为“目标对象”.

~~~~~~~~~~
cd <object-id>
~~~~~~~~~~

> Into the instance of class 'com.chenjw.knife.server.test.impl.ApplyServiceImpl'

![cd](cd.jpg)


## ls

这个命令用来遍历“目标对象”的属性值或方法。

~~~~~~~~~~
ls [-f] [-m] [<classname>]
~~~~~~~~~~

 <h3>classname</h3>    
                 
如果参数包含了 <classname> ，会查找该类的静态属性和静态方法 ，如果没有包含，会查找“目标对象”的实例属性和方法。

 <h3>-f</h3>       
                     
列出“目标对象”或“目标类”的所有静态或非静态属性值，包含了在父类中定义的属性。

![ls -f](ls_f.jpg)

 <h3>-m</h3>     
                    
列出“目标对象”或“目标类”的所有静态或非静态方法，包含了在父类中定义的方法。

![ls -m](ls_m.jpg)

 <h3>-c</h3>     
                    
列出“目标对象”或“目标类”的所有构造方法。

## set

修改属性值。

~~~~~~~~~~
set [-s] <fieldname> <new-value>
~~~~~~~~~~

 <h3>fieldname</h3>

可以输入'类名.属性名' 来表示设置静态变量值。或直接输入 '属性名' 来表示 设置“目标对象”的静态或非静态方法。

 <h3>new-value</h3>                     

一个表达式表示要设置的值，如果为json格式，则会根据目标属性的类型做自动转换。 如果为 '@' 加数字的类型，则表示直接设置为该对象id对应的对象.

 <h3>-s</h3>

强制表示设置静态属性，用来避免误解。


> 通过json表达式的方式设置属性值。

![通过json表达式的方式设置属性值](set.jpg)

> 使用对象id的方式设置属性值。

![使用对象id的方式设置属性值](set_by_object_id.jpg)


## invoke

这个命令用来调用某个方法，并且跟踪这个方法调用内部的执行情况。

~~~~~~~~~~
invoke [-f <filter-expretion>] [-t] <invoke-expretion>
~~~~~~~~~~

 <h3>invoke-expretion</h3>

输入 '类名.方法名(参数1,参数2...)' 的格式可以调用一个静态方法, 也可以输入 '方法名(参数1,参数2...)' 的格式来调用目标对象的某个方法. 参数可以使用json表达式，也可以使用'@'加对象id。

 <h3>-f <filter-expretion></h3>         

设置 <filter-expretion> 可以用来过滤掉方法调用输出中不需要显示的内容。

 <h3>-t</h3>                            

如果设置了 '-t' 则会跟踪所有方法内部的调用。比如方法 'a' 中如果调用了方法 'b'，则在 'invoke a' 时，也会递归输出 'b' 中的执行情况。

> 不跟踪方法内部调用的情况

![invoke](invoke.jpg)

> 跟踪方法内部调用的情况

![invoke -t](invoke_t.jpg)

> 跟踪方法内部调用，并只显示符合过滤条件的调用

![invole -t -f](invoke_f_t.jpg)

## trace

这个命令用来等待某个符合条件的方法调用（如通过类名和方法名匹配），当符合条件的调用发生时会输出方法调用的过程、入参、返回值、响应时间等参数。

~~~~~~~~~~
trace [-f <filter-expretion>] [-n <trace-num>] [-t] <trace-expretion>
~~~~~~~~~~

 <h3>trace-expretion</h3>

输入 '类名.方法名' 的格式表示跟踪静态方法的调用 , 或输入 '方法名' 的格式表示跟踪 '目标对象' 的某个方法调用。

 <h3>-f <filter-expretion></h3>         

设置 <filter-expretion> 可以用来过滤掉方法调用输出中不需要显示的内容。

 <h3>-t</h3>                            

如果设置了 '-t' 则会跟踪所有方法内部的调用。比如方法 'a' 中如果调用了方法 'b'，则在 'invoke a' 时，也会递归输出 'b' 中的执行情况。

 <h3>-n <trace-num></h3>                            

跟踪方法调用的次数，如果没有指定则只会记录第一次方法调用。

## ref

列出堆内存中所有引用到该对象的对象。

~~~~~~~~~~
ref [-r] <object-id>
~~~~~~~~~~

 <h3>object-id</h3>                     

目标对象的id，如果没有设置，就会默认使用当前对象。

> 列出引用到指定对象的所有对象.

![列出引用到指定对象的所有对象](ref.jpg)

 <h3>-r</h3>

查找该对象引用到的对象，而不是引用到该对象的对象。

## top

查找最占cpu的线程。或者查找存在引用数最多的对象。按从多到少排序。

~~~~~~~~~~
top [-n <num>] [<type>]
~~~~~~~~~~

 <h3>type</h3>                     

"ref":查找存在引用数最多的对象。

"thread":查找最占cpu的线程。

 <h3>-n</h3>                     

需要列出对象的数量，默认为10个。

# 安全性

## native代码bug（如内存越界）问题引起crash。  

native代码单元测试保证，覆盖大多数分支。

## native代码泄漏，如申请的内存没有释放。

？？

## agent加载的class或引用的对象没有释放。

最小agent，classloader隔离，自检测。

## 端口号释放

心跳，自动终止socket连接。



