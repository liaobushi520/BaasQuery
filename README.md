# BaasQuery
BaasQuery简化了客户端对baas数据库的查询操作。
## 对比
 
 
例如：查询学生表中年龄小于8岁，或大于10岁，且名字中包含'李',或'王'的,结果集合按名字升序排序，且最多返回100条.
</br>一般的baas查询代码
```Java 
 DroiCondition ageCondition = DroiCondition.cond("age", DroiCondition.Type.LT, 5)
                              .or(DroiCondition.cond("age", DroiCondition.Type.GT, 10)); 
 DroiCondition nameCondition = DroiCondition.cond("name", DroiCondition.Type.CONTAINS, "李")
                               .or(DroiCondition.cond("name",  DroiCondition.Type.CONTAINS, "王"));
 DroiCondition condition = ageCondition.and(nameCondition);
 DroiQuery.Builder builder = DroiQuery.Builder.newBuilder().query("table_student")
                             .where(condition).limit(50).orderBy("name",true);
 DroiError droiError=new DroiError();
 List<Student> students=builder.build().runQuery(droiError);  
```
BaasQuery查询代码
```Java
@Service
public interface BaasService{
       @Query(table="table_student",
       condition="(age<$age||age>10)&&(name CANTAINS $name || name CANTAINS'王')",limit="100",orderBy("name"))
       List<Student> listStudents(int age,String name);
}
final BaasService baasService = BaasQuery.query(BaasService.class);
List<Students> students=baasServise.listStudents(5,"李");
```
代码解释:@Service会在编译期生成BaasServiceImp类，@Query会为该类实现被注解的方法。table指定表名，condition指定查询条件(使用$参数名可以引用方法中的参数，例如$age),limit指定需要返回的数据集数量，orderBy指定排序规则。

**需要注意的是以上的查询代码实在主线程中执行，要想异步调用可以将返回值修改为BaasCall\<List\<Student\>\>,通过BaasCall执行异步查询。**

## 实现原理
如果使用过Retrofit，一定会对BaaQuery的实现模式不会陌生。没错，BaasQuery的灵感正是来源于Retrofit。只是在实现原理上有所不同，Retrofit利用的是反射和动态代理技术，而BaasQuery则是利用注解，编译期代码生成技术。不使用反射和动态代理，并不是因为考虑到反射的运行时开销，而是因为达不到我的目的。因为我需要在注解中引用方法参数，而这是用反射是做不到的。

## 待改进的地方
由于我对正则表达式并不是很熟悉，所以现阶段条件语句只支持一级括号(A&&B||C&&D)||E&&F,如果你查询条件很复杂，比如(A||B&&(C||D&&E||F))||(G&&H||M&&N),很遗憾，BaasQuery做不到。
介于自身水平限制，在此我也希望有兴趣的公司同仁可以参与改进。 万分感谢！！！！

