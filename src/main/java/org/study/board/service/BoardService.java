package org.study.board.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;
import org.study.board.dto.User;
import org.study.board.repository.BoardMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BoardService {

    // 파일 다운로드
    private static final String FILE_DIRECTORY = "d:\\image";
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

    public List<Board> getChildBoards(Integer bno) {
        return mapper.getChildBoards(bno);
    }

    public int hit(Integer bno) {
        return mapper.hit(bno);
    }

    public Integer cntBoard(Integer boardType) {return mapper.cntBoard(boardType);}

    public void insertBoard(Board board){
        // 인증된 현재 사용자 정보 받아옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            currentUserId = user.getIdx();
            board.setUserIdx(currentUserId);
        }
        if (board.getBno() != null) {
            // 기존 게시글의 작성자 불러옴
            Board existingBoard = mapper.getBoard(board.getBno());
            Long originalWriterId = existingBoard.getUserIdx();

            if (currentUserId != null && currentUserId.equals(originalWriterId)) {
                // 작성자가 현재 사용자와 동일할 경우에만 업데이트 수행
                mapper.deleteFile(board);
                mapper.updateBoard(board);
            }
        } else{
            if (board.getParentBno() != null) {
                Board parentBoard = mapper.getBoard(board.getParentBno());
                board.setGno(parentBoard.getGno());
                board.setSorts(parentBoard.getSorts() + 1);
                board.setDepth(parentBoard.getDepth() + 1);
                mapper.updateSorts(parentBoard.getGno(), parentBoard.getSorts());
            } else if (board.getBoardType() == 1) { //QnA(계층형) 게시판
                Integer maxGno = mapper.getMaxGno();
                board.setGno(maxGno + 1);
                board.setSorts(0);
                board.setDepth(0);
            } else{ //공지사항(일반) 게시판
                board.setGno(0);
                board.setSorts(0);
                board.setDepth(0);
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

    public ResponseEntity<Resource> downloadFile(String uuid, String filename) throws IOException {
        Path filePath = Paths.get(FILE_DIRECTORY, uuid + "_" + filename);
        Resource resource = new InputStreamResource(Files.newInputStream(filePath, StandardOpenOption.READ));

        String contentType = Files.probeContentType(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @Transactional
    public boolean deleteBoard(Integer bno){
        // 하위 게시글 삭제
        mapper.deleteChildBoards(bno);
        return mapper.deleteBoard(bno);
    }
}
