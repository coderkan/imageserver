package com.coderkan.imageserver.payload;

import com.coderkan.imageserver.models.Image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {
	private String uuid;
	private String fileName;
	private String fileType;
	private long size;

	public ImageResponse(Image image) {
		setUuid(image.getUuid());
		setFileName(image.getFileName());
		setFileType(image.getFileType());
		setSize(image.getSize());
	}
}