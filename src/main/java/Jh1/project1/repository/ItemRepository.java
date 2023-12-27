package Jh1.project1.repository;

import Jh1.project1.domain.Item;

import java.util.List;

public interface ItemRepository {

    Item save(Item item);

    Item findById(Long itemId);

    List<Item> findAll();

    void update(Long itemId, Item updateItem);

    void clearStore();

}
