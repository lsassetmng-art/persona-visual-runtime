package com.lsam.visualruntime.anim;

/**
 * Phase B 正規入力（layersJson）をフレームごとに生成するためのProvider。
 * Lv4以降は、ここで口パク/瞬きを反映したJSONを返す。
 */
public interface LayersJsonProvider {
    String provide(long frameIndex, long nowMs);
}
