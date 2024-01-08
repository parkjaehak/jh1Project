package Jh1.project1.controller;

import Jh1.project1.domain.DeliveryCode;
import Jh1.project1.domain.Item;
import Jh1.project1.domain.ItemType;
import Jh1.project1.domain.UploadFile;
import Jh1.project1.dto.item.ItemSaveDto;
import Jh1.project1.dto.item.ItemUpdateDto;
import Jh1.project1.dto.item.ItemUploadDto;
import Jh1.project1.repository.ItemRepository;
import Jh1.project1.service.StoreFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final StoreFileService storeFileService;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "item/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "item/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "item/addForm";
    }

    @PostMapping("/add")
    public String add(@Validated @ModelAttribute("item") ItemSaveDto itemSaveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //특정 필드 예외가 아닌 전체 예외
        if (itemSaveDto.getPrice() != null && itemSaveDto.getQuantity() != null) {
            int resultPrice = itemSaveDto.getPrice() * itemSaveDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "item/addForm";
        }

        //Form(dto)데이터 기반 Item 객체 변환과정
        Item item = new Item();
        item.setItemName(itemSaveDto.getItemName());
        item.setPrice(itemSaveDto.getPrice());
        item.setQuantity(itemSaveDto.getQuantity());
        item.setOpen(itemSaveDto.getOpen());
        item.setRegions(itemSaveDto.getRegions());
        item.setItemType(itemSaveDto.getItemType());
        item.setDeliveryCode(itemSaveDto.getDeliveryCode());

        log.info("itemSaveDto.getRegions() = {}", itemSaveDto.getRegions());
        log.info("itemSaveDto.getItemType() = {}", itemSaveDto.getItemType());
        log.info("itemSaveDto.getDeliveryCode() = {}", itemSaveDto.getDeliveryCode());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item",findItem);
        return "item/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateDto itemUpdateDto, BindingResult bindingResult) {

        //특정 필드 예외가 아닌 전체 예외
        if (itemUpdateDto.getPrice() != null && itemUpdateDto.getQuantity() != null) {
            int resultPrice = itemUpdateDto.getPrice() * itemUpdateDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "item/editForm";
        }

        Item updateItem = new Item();
        updateItem.setItemName(itemUpdateDto.getItemName());
        updateItem.setPrice(itemUpdateDto.getPrice());
        updateItem.setQuantity(itemUpdateDto.getQuantity());
        updateItem.setOpen(itemUpdateDto.getOpen());
        updateItem.setRegions(itemUpdateDto.getRegions());
        updateItem.setItemType(itemUpdateDto.getItemType());
        updateItem.setDeliveryCode(itemUpdateDto.getDeliveryCode());

        itemRepository.update(itemId, updateItem);

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/delete")
    public String delete(@PathVariable Long itemId) {
        itemRepository.delete(itemId);
        return "redirect:/items";
    }

    @GetMapping("/{itemId}/upload")
    public String uploadForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);

        model.addAttribute("item",findItem);
        return "item/uploadForm";
    }

    @PostMapping("/{itemId}/upload")
    public String upload(@PathVariable Long itemId, @ModelAttribute ItemUploadDto itemUploadDto, RedirectAttributes redirectAttributes, Model model) throws IOException {

        Item findItem = itemRepository.findById(itemId);

        UploadFile attachFile = storeFileService.storeFile(itemUploadDto.getAttachFile());
        List<UploadFile> ImageFiles = storeFileService.storeFiles(itemUploadDto.getImageFiles());

        //데이터베이스에 저장
        findItem.setItemBrand(itemUploadDto.getItemBrand());
        findItem.setAttachFile(attachFile);
        findItem.setImageFiles(ImageFiles);
        //itemRepository.save(findItem);

        model.addAttribute("item", findItem);

//        redirectAttributes.addAttribute("itemId", item.getId());
//        return "redirect:/items/{itemId}";
        return "item/uploadView";
    }

    // 파일 이미지 조회
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable(name = "filename") String storeFileName) throws MalformedURLException {
        log.info("storeFileName={}",storeFileName);
        return new UrlResource("file:" + storeFileService.getFullPath(storeFileName));
    }

    // 파일 이미지 다운로드
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {

        Item findItem = itemRepository.findById(itemId);
        String storeFileName = findItem.getAttachFile().getStoreFileName();
        String uploadFileName = findItem.getAttachFile().getUploadFileName();
        log.info("uploadFileName={}",uploadFileName);

        // body
        UrlResource resource = new UrlResource("file:" + storeFileService.getFullPath(storeFileName));

        // header (파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다)
        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }






   //--------------------------------------------------------------------//

    @ModelAttribute("regions")
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("SEOUL", "서울");
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");
        return regions;
    }

    @ModelAttribute("itemTypes")
    public ItemType[] itemTypes() {
        return ItemType.values();
    }

    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }
}
