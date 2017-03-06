1.request 客户端需要针对每个请求 创建一个request的子类，重写方法:
+getCall() 获取retrofit 执行句柄
+adaptStructForCache()  将服务器返回的单一map结构映射为缓存的结构（ui需要的结构）
+cacheNetResponse() 缓存解析好的数据
+perform() 执行请求策略

2.将request实例交给RequestManager 管理,执行请求发送逻辑,RequestManager.getInstance().enqueueRequest()

3.数据请求结果通过RequestControlableActivity 子类的handlexxxx()回调，回调到客户端，客户端根据请求类型执行不同的操作

4.取消请求可以通过RequestManager.getInstance().cancelRequest()

5.客户端如果需要返回bean，需要实现getTypeReference（）方法 eg:
public TypeReference&lt;User&gt getTypeReference(){
return new TypeReference&lt;User&gt(){};
}
