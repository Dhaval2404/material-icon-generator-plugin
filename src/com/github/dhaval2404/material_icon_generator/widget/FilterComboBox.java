package com.github.dhaval2404.material_icon_generator.widget;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright 2014-2015 Material Design Icon Generator (Yusuke Konishi)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file was modified by the author from the following original file:
 * https://raw.githubusercontent.com/konifar/android-material-design-icon-generator-plugin/master/src/main/java/com/konifar/material_icon_generator/FilterComboBox.java
 *
 */
public class FilterComboBox extends JComboBox<String> {

    private List<String> comboBoxList;

    public FilterComboBox() {
        this(new ArrayList<>());
    }

    public FilterComboBox(List<String> comboBoxList) {
        super(comboBoxList.toArray(new String[0]));

        this.comboBoxList = comboBoxList;
        this.setEditable(true);

        initListener();
    }

    private void initListener() {
        final JTextField textField = (JTextField) this.getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_ESCAPE:
                        requestFocus(false);
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        break;
                    default:
                        SwingUtilities.invokeLater(() -> filter(textField.getText()));
                }
            }
        });

        getAccessibleContext().addPropertyChangeListener(event -> {
            if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                    && AccessibleState.FOCUSED.equals(event.getNewValue())
                    && getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                ComboPopup popup = (ComboPopup) getAccessibleContext().getAccessibleChild(0);
                JList list = popup.getList();

                if (list.getSelectedValue() != null) {
                    setSelectedItem(String.valueOf(list.getSelectedValue()));
                }
            }
        });
    }

    public void filter(String inputText) {
        List<String> filterList = new ArrayList<String>();
        for (String text : comboBoxList) {
            if (text.toLowerCase().contains(inputText.toLowerCase())) {
                filterList.add(text);
            }
        }
        if (!filterList.isEmpty()) {
            setModel(new DefaultComboBoxModel(filterList.toArray()));
            setSelectedItem(inputText);
            showPopup();
        } else {
            hidePopup();
        }
    }

    public String getInputText() {
        return ((JTextField) this.getEditor().getEditorComponent()).getText();
    }

    public void addItem(String text) {
        super.addItem(text);
        comboBoxList.add(text);
    }

    /**
     * Check if item exist in provided ComboBox Options
     *
     * @return item name if exist else return null
     */
    public String getSelectedItemText() {
        String item = (String) super.getSelectedItem();
        if(comboBoxList.contains(item)){
            return item;
        }
        else return null;
    }
}