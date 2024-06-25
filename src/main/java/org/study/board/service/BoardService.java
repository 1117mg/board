package org.study.board.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;
import org.study.board.repository.BoardMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BoardService {

    @Autowired
    private BoardMapper mapper;

    public List<Board> getBoardlist(Board board) {
        return mapper.getBoardList(board);
    }

    public List<Board> getQnaList(Board board) {
        return mapper.getQnaList(board);
    }

    // 첨부파일 리스트
    public List<FileDto> getFile (Board board) {return mapper.getFile(board);}

    public Board getBoard(Integer bno){
        return mapper.getBoard(bno);
    }

    public List<Board> getParentBoards(Integer bno) {
        return mapper.getParentBoards(bno);
    }

    public int hit(Integer bno) {
        return mapper.hit(bno);
    }

    public Integer cntBoard(Integer boardType) {return mapper.cntBoard(boardType);}

    public void insertBoard(Board board){
        if (board.getBno() != null) {
            mapper.deleteFile(board);
        }
        if(board.getBno()!=null){
            mapper.updateBoard(board);
        }else{
            if (board.getParentBno() != null) {
                Board parentBoard = mapper.getBoard(board.getParentBno());
                board.setGno(parentBoard.getGno());
                board.setSorts(parentBoard.getSorts() + 1);
                board.setDepth(parentBoard.getDepth() + 1);
                mapper.updateSorts(parentBoard.getGno(), parentBoard.getSorts());
            }
            mapper.insertBoard(board);
        }
        // 파일 이름 유니크하게 생성
        List<FileDto> list = new ArrayList<>();
        String[] uuids = board.getUuids();
        String[] fileNames = board.getFilenames();
        String[] contentTypes = board.getContentTypes();

        if(uuids!=null){
            for(int i=0;i<uuids.length;i++){
                FileDto fileDto = new FileDto();
                fileDto.setFilename(fileNames[i]);
                fileDto.setUuid(uuids[i]);
                fileDto.setContentType(contentTypes[i]);
                list.add(fileDto);
            }
        }

        // 첨부파일 등록 (게시글이 있을경우)
        if (!list.isEmpty()) {
            board.setList(list);
            mapper.insertFile(board);
        }
    }

    // 파일 다운로드
    public ResponseEntity<Resource> downloadFile(FileDto fileDto) throws IOException {
        // 파일 저장 경로 설정
        String filePath = "d:\\image";
        Path path = Paths.get(filePath + "/" + fileDto.getUuid() + "_" + fileDto.getFilename());
        String contentType = Files.probeContentType(path);
        // header를 통해서 다운로드 되는 파일의 정보를 설정한다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileDto.getFilename(), StandardCharsets.UTF_8)
                .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }


    public boolean deleteBoard(Integer bno){
        return mapper.deleteBoard(bno);
    }

}
