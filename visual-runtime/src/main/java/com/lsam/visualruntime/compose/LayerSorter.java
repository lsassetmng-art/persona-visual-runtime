package com.lsam.visualruntime.compose;

import com.lsam.visualruntime.model.LayerSpec;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LayerSorter {

    public void sortByZIndex(List<LayerSpec> layers) {

        if (layers == null || layers.size() <= 1) {
            return;
        }

        Collections.sort(layers, new Comparator<LayerSpec>() {
            @Override
            public int compare(LayerSpec a, LayerSpec b) {
                return Integer.compare(a.getZIndex(), b.getZIndex());
            }
        });
    }
}
