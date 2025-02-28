package com.outsourcingdelivery.domain.menu.controller;

import com.outsourcingdelivery.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
}
