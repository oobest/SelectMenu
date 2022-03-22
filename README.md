# SelectMenu
Android SelectMenu

Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency
```
dependencies {
	        implementation 'com.github.oobest:SelectMenu:v1.0.0'
}
```

String 列表数据传入使用
```
SelectMenu.create(getSupportFragmentManager(), view, stringDataList)
        .setSelectedItem(defaultSelect) // 默认选中项
        .show(o -> {
            BridgeData temp = o;
            if (temp.equals(currentBridge)) {
                // 点击了已选中项，取消选项
                temp = null;
            }
            currentBridge = temp;
            ((TextView) view).setText(currentBridge.getName());
        });
```

对象list数据传入使用方法
```
SelectMenu.create(getSupportFragmentManager(), view, bridgeDataList)
        .setItemTextMapper(BridgeData::getName) // 下拉菜单显示项
        .setIsSameFunction((o1, o2) -> o1.getId() == o2.getId()) // 判断o1,o2是否为同一个对象
        .setSearchable(bridgeDataList.size()>5) //bridgeDataList数量大于5时，打开搜索框
        .setSelectedItem(currentBridge) // 默认选中项
        .show(o -> {
            BridgeData temp = o;
            if (temp.equals(currentBridge)) {
                // 点击了已选中项，取消选项
                temp = null;
            }
        currentBridge = temp;
        ((TextView) view).setText(currentBridge.getName());
        });
```
