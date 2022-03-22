# SelectMenu
Android SelectMenu

使用方法
'''
SelectMenu.create(getSupportFragmentManager(), view, bridgeData)
        .setItemTextMapper(BridgeData::getName) // 下拉菜单显示项
        .setIsSameFunction((o1, o2) -> o1.getId() == o2.getId()) // 判断o1,o2是否为同一个对象
        .setSelectedItem(currentBridge) // 默认选中项
        .show(o -> {
            BridgeData temp = o;
            if (temp.equals(currentBridge)) {
                // 点击了已选中项，取消选项
                temp = null;
            }
        currentBridge = temp;
        selectBridge.setText(currentBridge.getName());
        });
'''
