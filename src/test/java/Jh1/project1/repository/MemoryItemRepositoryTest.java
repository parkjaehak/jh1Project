package Jh1.project1.repository;


import Jh1.project1.domain.Item;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class MemoryItemRepositoryTest {

    MemoryItemRepository itemRepository = new MemoryItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }

    @Test
    void saveTest() {
        //given
        Item item = new Item("A", 5000, 5);
        //when
        Item savedItem = itemRepository.save(item);
        //then
        Item findItem = itemRepository.findById(item.getId());
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void findAllTest() {
        Item item1 = new Item("item1", 1000, 1);
        Item item2 = new Item("item2", 2000, 2);

        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> result = itemRepository.findAll();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(item1, item2);
    }

    @Test
    void updateTest() {
        Item item = new Item("item1", 1000, 1);
        Item savedItem = itemRepository.save(item);


        Item updateItem = new Item("item2", 2000, 2);
        itemRepository.update(savedItem.getId(), updateItem);

        Item findItem = itemRepository.findById(savedItem.getId());

        assertThat(findItem.getItemName()).isEqualTo(updateItem.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateItem.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateItem.getQuantity());

    }

}
