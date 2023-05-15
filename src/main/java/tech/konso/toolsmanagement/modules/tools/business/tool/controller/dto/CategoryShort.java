package tech.konso.toolsmanagement.modules.tools.business.tool.controller.dto;

import lombok.Builder;

@Builder
public record CategoryShort(Long id, String name) {
}
