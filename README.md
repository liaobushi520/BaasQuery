# BaasQuery
BaasQuery简化了客户端对baas数据库的查询操作。
## 对比
例如：查询学生表中年龄小于8岁，或大于10岁，且名字中包含'李',或'王'的,结果集合按名字升序排序，且最多返回100条.
</br>一般的baas查询代码
</br><font size=1>DroiCondition ageCondition = DroiCondition.cond("age", DroiCondition.Type.LT, 5).or(DroiCondition.cond("age", DroiCondition.Type.GT, 10));</font>
</br>DroiCondition nameCondition = DroiCondition.cond("name", DroiCondition.Type.CONTAINS, "李").or(DroiCondition.cond("name", DroiCondition.Type.CONTAINS, "王"));
</br>DroiCondition condition = ageCondition.and(nameCondition);
</br> DroiQuery.Builder builder = DroiQuery.Builder.newBuilder().query("table_student").where(condition).limit(50).orderBy("name",true);
</br>DroiError droiError=new DroiError();
</br>List<Student> students=builder.build().runQuery(droiError); 

</br>BaasQuery查询代码
</br>@Service
</br>public interface BaasService{
       </br>@Query(table="table_student",condition="(age<$age||age>10)&&(name CANTAINS $name || name CANTAINS '王')",limit="100",orderBy("name"))
       </br>List<Student> listStudents(int age,String name);
}

final BaasService baasService = BaasQuery.query(BaasService.class);
List<Students> students=baasServise.listStudents(5,"李");
