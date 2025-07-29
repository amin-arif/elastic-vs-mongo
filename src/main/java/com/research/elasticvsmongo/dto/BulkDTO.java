package com.research.elasticvsmongo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkDTO {
	@NonNull
	private DB dbName;

	@NonNull
	private Integer bulkSize;

	@NonNull
	private Integer chunkSize;
}
