package Jh1.project1.repository;

import Jh1.project1.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemoryItemRepository implements ItemRepository {

    private static final Map<Long, Item> memory = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Item save(Item item) {
        item.setId(++sequence);
        memory.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return memory.get(itemId);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public void update(Long itemId, Item updateItem) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateItem.getItemName());
        findItem.setPrice(updateItem.getPrice());
        findItem.setQuantity(updateItem.getQuantity());

        findItem.setOpen(updateItem.getOpen());
        findItem.setRegions(updateItem.getRegions());
        findItem.setItemType(updateItem.getItemType());
        findItem.setDeliveryCode(updateItem.getDeliveryCode());
    }

    @Override
    public void clearStore() {
        memory.clear();
    }
}
