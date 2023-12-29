package Jh1.project1.controller;

import Jh1.project1.domain.DeliveryCode;
import Jh1.project1.domain.Item;
import Jh1.project1.domain.ItemType;
import Jh1.project1.domain.UploadFile;
import Jh1.project1.dto.item.SaveDto;
import Jh1.project1.dto.item.UpdateDto;
import Jh1.project1.dto.item.UploadDto;
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
    public String add(@Validated @ModelAttribute("item") SaveDto saveDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //특정 필드 예외가 아닌 전체 예외
        if (saveDto.getPrice() != null && saveDto.getQuantity() != null) {
            int resultPrice = saveDto.getPrice() * saveDto.getQuantity();
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
        item.setItemName(saveDto.getItemName());
        item.setPrice(saveDto.getPrice());
        item.setQuantity(saveDto.getQuantity());
        item.setOpen(saveDto.getOpen());
        item.setRegions(saveDto.getRegions());
        item.setItemType(saveDto.getItemType());
        item.setDeliveryCode(saveDto.getDeliveryCode());

        log.info("saveDto.getRegions() = {}", saveDto.getRegions());
        log.info("saveDto.getItemType() = {}", saveDto.getItemType());
        log.info("saveDto.getDeliveryCode() = {}", saveDto.getDeliveryCode());

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
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") UpdateDto updateDto, BindingResult bindingResult) {

        //특정 필드 예외가 아닌 전체 예외
        if (updateDto.getPrice() != null && updateDto.getQuantity() != null) {
            int resultPrice = updateDto.getPrice() * updateDto.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "item/editForm";
        }

        Item updateItem = new Item();
        updateItem.setItemName(updateDto.getItemName());
        updateItem.setPrice(updateDto.getPrice());
        updateItem.setQuantity(updateDto.getQuantity());
        updateItem.setOpen(updateDto.getOpen());
        updateItem.setRegions(updateDto.getRegions());
        updateItem.setItemType(updateDto.getItemType());
        updateItem.setDeliveryCode(updateDto.getDeliveryCode());

        itemRepository.update(itemId, updateItem);

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}/upload")
    public String uploadForm(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);

        model.addAttribute("item",findItem);
        return "item/uploadForm";
    }

    @PostMapping("/{itemId}/upload")
    public String upload(@PathVariable Long itemId, @ModelAttribute UploadDto uploadDto, RedirectAttributes redirectAttributes, Model model) throws IOException {

        Item findItem = itemRepository.findById(itemId);

        UploadFile attachFile = storeFileService.storeFile(uploadDto.getAttachFile());
        List<UploadFile> ImageFiles = storeFileService.storeFiles(uploadDto.getImageFiles());

        //데이터베이스에 저장
        findItem.setItemBrand(uploadDto.getItemBrand());
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
