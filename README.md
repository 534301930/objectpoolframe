该项目的对象池包括两部分：
1.普通对象的对象池
2.池化对象的对象池

对象工厂的说明：
IObjectFactory定义的对象工厂的基本行为
IPooledObjectFactory是对上述的扩展，后续会根据实际需要添加一些特有方法
项目中有两个默认的实现类DefaultObjectFactory和DefaultPooledObjectFactory，
后者的行为基本与前者相似，只不过操作对象由普通对象变为池化对象.

池化对象：
池化对象在普通对象基础上做了一层包装，使其能够保存在对象池中的一些需要使用的状态和属性.
一般情况下不需要用到池化对象，但在某些情况下，如需要监控对象池中对象的信息时需要用到此对象.
池化对象有一个统一接口IPooledObject，有一个默认的实现类DefaultPooledObject供用户使用.
用户可在DefaultPooledObject类的基础上扩展一些属性，或者直接实现IPooledObject来重写所有行为.

对象池：
有一个统一接口IObjectPool定义对象池的一些公共行为，有两个默认的实现类DefaultObjectPool和
DefaultPooledObjectPool来分别对应普通对象的对象池和池化对象的对象池.
用户可以继承相应类或者实现相应接口来满足特殊要求

pom.xml：
项目预定义了编译和源码所需jdk版本，用户进行修改，更正成自己的jdk版本，然后maven -> update project...

日志：
项目中使用slf4j+logback作为日志系统，方便用户自行切换日志实现

demo：
项目使用样例在com.objectpool.test包下，用户可以在该包中进行一些简单测试

欢迎使用:)，如有问题可在github上提问，作者会不定期查看并解答.