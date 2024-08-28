function confirmAlarmRemove()
{
    var yes = confirm('Are you sure you want to remove alarm?');
    if (yes)
       return true;
    else
       return false;
}