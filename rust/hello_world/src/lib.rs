extern crate jni;

use jni::JNIEnv;
use jni::objects::*;
use jni::sys::{jint, jobject, jstring};
use std::os::raw::c_char;
use std::ffi::{CStr,CString};

#[no_mangle]
pub extern fn foo(a: i32, b: i32) ->i32 {
    println!("hello : a + b = {}", a + b);
    return a+b;
}


#[no_mangle]
pub extern fn boo(input: *const c_char) -> *const c_char  {
    let owned_string: String = "hello ".to_owned();
    let name = to_string(input);
    println!("Hello from Rust, {}{}", owned_string,name);
    let r =  to_ptr(format!("{}, {}!", owned_string,name));
    return r;
}


/// Convert a native string to a Rust string
fn to_string(pointer: *const c_char) -> String {
    let slice = unsafe { CStr::from_ptr(pointer).to_bytes() };
    std::str::from_utf8(slice).unwrap().to_string()
}

/// Convert a Rust string to a native string
fn to_ptr(string: String) -> *const c_char {
    let cs = CString::new(string.as_bytes()).unwrap();
    let ptr = cs.as_ptr();
    // Tell Rust not to clean up the string while we still have a pointer to it.
    // Otherwise, we'll get a segfault.
    std::mem::forget(cs);
    ptr
}


#[no_mangle]
pub extern "system" fn Java_org_example_Main_hello<'local>(mut env: JNIEnv<'local>,class: JClass<'local>,input: JString<'local>) -> jstring  {
    let input: String =
        env.get_string(&input).expect("Couldn't get java string!").into();

    // Then we have to create a new java string to return. Again, more info
    // in the `strings` module.
    let output = env.new_string(format!("Hello, {}!", input))
        .expect("Couldn't create java string!");

    // Finally, extract the raw pointer to return.
    return output.into_raw();
}