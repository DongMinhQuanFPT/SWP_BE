package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Blogs;
import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import com.SWP391.KoiXpress.Model.request.Blog.CreateBlogRequest;
import com.SWP391.KoiXpress.Model.response.Blog.AllBlogResponse;
import com.SWP391.KoiXpress.Model.response.Blog.CreateBlogResponse;
import com.SWP391.KoiXpress.Model.response.Blog.DeleteBlogResponse;
import com.SWP391.KoiXpress.Model.response.Blog.UpdateBlogResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Repository.BlogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;


    public CreateBlogResponse createBlog(CreateBlogRequest createBlogRequest) {
        Blogs blogs = new Blogs();
        Users users = authenticationService.getCurrentUser();
        blogs.setImg(createBlogRequest.getImg());
        blogs.setPost(createBlogRequest.getPost());
        blogs.setUsers(users);
        blogRepository.save(blogs);
        //
        EachUserResponse eachUserResponse = modelMapper.map(users, EachUserResponse.class);
        CreateBlogResponse createBlogResponse = new CreateBlogResponse();
        createBlogResponse.setEachUserResponse(eachUserResponse);
        createBlogResponse.setBlogId(blogs.getId());
        createBlogResponse.setPost(blogs.getPost());
        createBlogResponse.setImg(blogs.getImg());

        return createBlogResponse;
    }

    public PagedResponse<AllBlogResponse> getAllBlog(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Blogs> blogsPage = blogRepository.findAll(pageRequest);

        List<AllBlogResponse> allBlogResponses = new ArrayList<>();
        for (Blogs blogs : blogsPage.getContent()) {
            Users users = blogs.getUsers();
            AllBlogResponse allBlogResponse = new AllBlogResponse();
            EachUserResponse eachUserResponse = modelMapper.map(users, EachUserResponse.class);
            allBlogResponse.setBlogId(blogs.getId());
            allBlogResponse.setImg(blogs.getImg());
            allBlogResponse.setPost(blogs.getPost());
            allBlogResponse.setEachUserResponse(eachUserResponse);
            allBlogResponses.add(allBlogResponse);
        }

        return new PagedResponse<>(allBlogResponses,page, size, blogsPage.getTotalElements(), blogsPage.getTotalPages(),blogsPage.isLast());
    }

    public DeleteBlogResponse delete(long blogId) {
        Blogs blogs = getBlogById(blogId);
        blogs.setDeleted(true);
        blogRepository.save(blogs);
        return modelMapper.map(blogs, DeleteBlogResponse.class);
    }

    public UpdateBlogResponse update(long blogId, Blogs blogs) {
        Blogs oldBlogs = getBlogById(blogId);
        oldBlogs.setPost(blogs.getPost());
        oldBlogs.setImg(blogs.getImg());
        blogRepository.save(oldBlogs);
        return modelMapper.map(oldBlogs, UpdateBlogResponse.class);
    }


    private Blogs getBlogById(long blogId) {
        Blogs oldBlogs = blogRepository.findBlogsById(blogId);
        if (oldBlogs == null) {
            throw new EntityNotFoundException("Blog not found!");
        }
        if (oldBlogs.isDeleted()) {
            throw new EntityNotFoundException("Blog not found!");
        }
        return oldBlogs;
    }
}
