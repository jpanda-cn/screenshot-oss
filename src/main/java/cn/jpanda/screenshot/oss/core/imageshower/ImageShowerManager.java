package cn.jpanda.screenshot.oss.core.imageshower;

import cn.jpanda.screenshot.oss.common.toolkit.ImageShower;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.Collection;
import java.util.Iterator;

public class ImageShowerManager implements Collection<ImageShower> {

    /**
     * 缓存所有的图片展示器
     */
    @Getter
    private ObservableList<ImageShower> showers = FXCollections.observableArrayList();


    @Override
    public int size() {
        return showers.size();
    }

    @Override
    public boolean isEmpty() {
        return showers.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return showers.contains(o);
    }

    @Override
    public Iterator<ImageShower> iterator() {
        return showers.iterator();
    }

    @Override
    public Object[] toArray() {
        return showers.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return showers.toArray(a);
    }

    @Override
    public boolean add(ImageShower imageShower) {
        return showers.add(imageShower);
    }

    @Override
    public boolean remove(Object o) {
        return showers.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return showers.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ImageShower> c) {
        return showers.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return showers.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return showers.retainAll(c);
    }

    @Override
    public void clear() {
        showers.clear();
    }
}
